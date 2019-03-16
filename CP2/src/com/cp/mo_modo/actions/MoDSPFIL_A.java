package com.cp.mo_modo.actions;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.cp._comun.ActionForm;
import com.cp._comun.ConfigPantalla;
import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;
import com.cp.mo_modo.bean.MoBean;
import com.cp.mo_modo.bean.MoBeanFiltro;
import com.cp.mo_modo.db.MoAccesoBaseDatos;
import com.cp.mo_modo.forms.MoRCD_AF;

import net.sf.json.JSONObject;

public class MoDSPFIL_A { //extends org.apache.struts.action.Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm  form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String resultado = "OK";
		
		MoRCD_AF pantalla = (MoRCD_AF)form;

		//////////////////////////////////////
		// Para permitir llamada de la versi�n en "Modal AngularJS" 
		boolean isVersionAngular = Subrutinas.isVersionAngular(request, form);	// (CARGA EL ACTION FORM DESDE EL PAYLOAD DE LA LLAMADA)
		//////////////////////////////////////

		String opcion = pantalla.getOpcionPantalla();
		/////////////////////////
		// Sincronizar con su sesi�n en servidor:
		/////////////////////////
		sincroSesion( request, form );  // Arrastre del usuario y sincronizaci�n de estados.
		// La pantalla debe poder ser "aut�noma" para que permita seguir funcionando aunque haya caducado su sesi�n en el servidor.
		// Act�a como repositorio de estados para poder regenerar la sesi�n si ha caducado.

		/////////////////////////
		// Seguridad de acceso al programa:
		/////////////////////////
		String proceso = (this.getClass().toString()).substring((this.getClass().toString()).lastIndexOf(".")+1);
		String usuario = pantalla.getLogon_USR();
		Subrutinas subrutinas = new Subrutinas();
		if ( ! subrutinas.controlAcceso( subrutinas.getBDConexion(request), usuario, proceso ) ) {
			resultado = "ERROR";
			ActionMessages errores = new ActionMessages();
			errores.add("error", new ActionMessage( "errors.detail", "'" + usuario + "' no autorizado a '" + proceso + "'." ));
//			saveErrors(request,errores);

			if (isVersionAngular) { Subrutinas.returnActionVersionAngular(request, response, this, false, null); return null; } // No navega con struts

			return mapping.findForward(resultado);
		}

		/////////////////////////
		// Despacho de la acci�n:
		/////////////////////////
		if ( opcion == null )
			resultado = this.cargarPantalla( request, pantalla );
		else {
			if ( opcion.trim().length()==0 ) {
				resultado = this.cargarPantalla( request, pantalla );
			} else {
				if ( opcion.trim().substring(0,4).equalsIgnoreCase("Edit") ) {
					resultado = opcion_Edit(request,form);
				} else if ( opcion.trim().equalsIgnoreCase("Nuevo") ) {
					resultado = opcion_Nuevo(request,form);
				} else if ( opcion.trim().equalsIgnoreCase("Filtrar") ) {
					pantalla.setFilaInicioGrid(1);
					resultado = this.cargarPantalla( request, pantalla );
				} else if ( opcion.trim().equalsIgnoreCase("AvPg") ) {
					resultado = opcion_AvPg(request,form);
				} else if ( opcion.trim().equalsIgnoreCase("RtPg") ) {
					resultado = opcion_RtPg(request,form);
				} else if ( opcion.trim().equalsIgnoreCase("MarcarTodo") ) {
					resultado = opcion_Selec_Marcar(request,form);
				} else if ( opcion.trim().equalsIgnoreCase("DesMarcarTodo") ) {
					pantalla.setClavesMarcadas( null );
					resultado = this.cargarPantalla( request, pantalla );
				} else if ( opcion.trim().equalsIgnoreCase("Borrar") ) {
					resultado = opcion_Selec_Borrar( request, pantalla );
				} else if ( opcion.trim().equalsIgnoreCase("Exportar") ) {
					resultado = this.opcion_Exportar( request, pantalla );
				} else if ( opcion.trim().equalsIgnoreCase("Grabar") ) {
					persistirPosiblesCambios(request,form);
					resultado = this.cargarPantalla( request, pantalla );
				///////////////////////////////////////
				} else if ( opcion.trim().length() > 10 && opcion.trim().substring(0,10).equalsIgnoreCase("colectivo_") ) {
					resultado = opcion_Selec_ChgColectivo( request, form );
				///////////////////////////////////////
				} else {
					resultado = this.cargarPantalla( request, pantalla );
				}
			}
		}

		//////////////////////////////////////
		// Para permitir llamada de la versi�n en "Modal AngularJS" 
		if ( isVersionAngular ) {
			if ( null != (ActionMessages) request.getAttribute( "org.apache.struts.action.ERROR" )) {
				Subrutinas.returnActionVersionAngular(request, response, this, false, null);
			} else {
				
				JSONObject json = new JSONObject();
				if ( Subrutinas.ActionFormToJson(form, json) ) {
					Subrutinas.returnActionVersionAngular(request, response, this, true, json.toString());
				} else {
					Subrutinas.returnActionVersionAngular(request, response, this, false, "Fallo en ActionFormToJson().");
				};
				
			}
			return null;	// No navega con struts
		}
		//////////////////////////////////////

		return mapping.findForward(resultado);
	}
	
	private String cargarPantalla( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		aplicarParametrosDeEntrada( request, form );
		inicializarPantalla( request, pantalla );
		///////////////////////////////////////////
		MoAccesoBaseDatos db = new MoAccesoBaseDatos();
		///////////////////////////////////////////
		ConfigPantalla cfg = new ConfigPantalla();
		cfg.setNombrePantalla( (this.getClass().toString()).substring((this.getClass().toString()).lastIndexOf(".")+1) );
		cfg.setTituloPantalla("modo");
		if ( pantalla.getFilasGrid() > 0 )	  // Para primera llamada...
			cfg.setFilasGrid( pantalla.getFilasGrid() );
		if ( pantalla.getFilaInicioGrid() > 0 ) // Para primera llamada...
			cfg.setFilaInicioGrid( pantalla.getFilaInicioGrid() );
		//////////////////////////////////////
		// �Es petici�n de exportaci�n?
		if ( pantalla.getOpcionPantalla() != null && pantalla.getOpcionPantalla().trim().equalsIgnoreCase("Exportar") ) {
			cfg.setExportar(true);
			// Determinaci�n de nombre del archivo de exportaci�n
			// (Debe quedar guardado en sesi�n, dentro del cfg destinado a pantalla)
			String nombre =
					request.getServletContext().getRealPath( "/" ) + 
					"x" +
					(this.getClass().toString()).substring((this.getClass().toString()).lastIndexOf(".")+1) +
					"_" +
					pantalla.getLogon_USR().trim() +
					"_" +
					Subrutinas.getFecha_aammdd() + 
					Subrutinas.getHora_HHMMSS() +
					".xls";
			cfg.setTituloPantalla( nombre );  // Nombre del archivo de exportaci�n sin extensi�n.
		}
		//////////////////////////////////////
		try {
			pantalla.setFilasGrid( cfg.getFilasGrid() );
			pantalla.setFilaInicioGrid( cfg.getFilaInicioGrid() );
			pantalla.setGrid( db.mo_getSeq(
					new Subrutinas().getBDConexion(request)
					, cfg
					, pantalla.getMo_filtro()
					
					)
					);
			pantalla.setFilasTotales( cfg.getFilasTotales() );
		} catch (StExcepcion ex) {
			resultado = "ERROR";
			ActionMessages errores = new ActionMessages();
			errores.add("error", new ActionMessage( "errors.detail", ex.getMessage() ));
//			saveErrors(request,errores);
		}
		///////////////////////////////////////////
		// Hace falta para el paginado:
		request.getSession(true).setAttribute( "cfgPantalla", cfg );
		///////////////////////////////////////////
		return resultado;
	}

	private void sincroSesion( HttpServletRequest request, ActionForm  form ) {
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		// Arrastre del usuario:
		String usr = (String)request.getSession().getAttribute("logon_USR");
		if ( usr != null && usr.trim().length() > 0 && !"null".equalsIgnoreCase(usr) ) { pantalla.setLogon_USR(usr); }
		request.getSession().setAttribute("logon_USR",pantalla.getLogon_USR());
		///////////////////////////////////////////
		// Meter aqui el resto de datos a mantener en sesion que se hallan guardado en la pantalla, o que vengan por request.
		// (Por ejemplo, pueden existir datos "restrictores a nivel de aplicaci�n" que han de ser permanentemente transportados entre
		//   pantalla<->sesi�n, pues deben aplicarse en todos y cada uno de los accesos a los datos...)
		Subrutinas.sincroSesion_COMUN(request,pantalla);
		///////////////////////////////////////////
	}
	
	private String opcion_AvPg( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		pantalla.setFilaInicioGrid( pantalla.getFilaInicioGrid() + pantalla.getFilasGrid() );
		resultado = this.cargarPantalla( request, pantalla );
		///////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_RtPg( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		int f = pantalla.getFilaInicioGrid() - pantalla.getFilasGrid();
		f = (f<1)?1:f;
		pantalla.setFilaInicioGrid( f );
		resultado = this.cargarPantalla( request, pantalla );
		///////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_Selec_Marcar( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		/////////////////////////////////////////////////
		resultado = this.cargarPantalla( request, form );
		if ( ! resultado.equalsIgnoreCase("OK") )
			return resultado;
		/////////////////////////////////////////////////
		if ( pantalla.getGrid() != null  ) {
			pantalla.setClavesMarcadas( null );
			pantalla.setClavesMarcadas( new String[pantalla.getGrid().length] );
			for (int i=0;i<pantalla.getGrid().length; i++) {
				MoBean reg = (MoBean) pantalla.getGrid()[i];
				// Guardar la CLAVE �NICA DEL SUBARCHIVO !!!
				pantalla.getClavesMarcadas()[i] = 
		reg.getMo_id_modo();
				// Ejemplo clave m�ltiple:
				// pantalla.getClavesMarcadas()[i] =
				//		 reg.getTf_AFACST() + "^" +
				//		 reg.getTf_AFAFCD() + "^" +
				//		 reg.getTf_AFAADA() + "^" +
				//		 reg.getTf_AFABDA() + "^" +
				//		 reg.getTf_AFAECD() + "^" +
				//		 reg.getTf_AFAICD()
				//		 ;
			}
		}
		/////////////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_Edit( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		String opcion = pantalla.getOpcionPantalla();
		opcion = opcion.trim().substring(4);
		///////////////////////////////////////////
		// Rescato la clave concatenada:
		MoBean key = new MoBean();
		String k;
	k = opcion; key.setMo_id_modo( k );
		///////////////////////////////////
		request.setAttribute("key_Mo",key);
		///////////////////////////////////
		resultado = "EDTRCD";
		///////////////////////////////////
		return resultado;
	}
	
	private String opcion_Nuevo( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		resultado = "ADDRCD";
		///////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_Exportar( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		// Ejecutar en modo "exportaci�n":
		resultado = this.cargarPantalla( request, pantalla );
		if (resultado.equalsIgnoreCase("OK")) {
			
			ConfigPantalla cfg = (ConfigPantalla) request.getSession(true).getAttribute( "cfgPantalla" );
			String NombreArchivo = "x";
			if ( cfg != null && cfg.getTituloPantalla() != null && cfg.getTituloPantalla().trim().length() > 0 ) {
				NombreArchivo = cfg.getTituloPantalla().trim();
				int i = NombreArchivo.lastIndexOf(java.io.File.separator);
				if ( i > -1 ) {
					try { 
						NombreArchivo = NombreArchivo.substring(i+1);
					} catch (Exception e) {;}
				}
			}
						
			// Recargar la pantalla:
			pantalla.setOpcionPantalla("RecargarGrid");
			resultado = this.cargarPantalla( request, pantalla );
			// Enlace al resultado:
			ActionMessages errores = new ActionMessages();
			errores.add("error", new ActionMessage( "errors.detail", "<a href='" + NombreArchivo + "' target='pepito'>Enlace a modo...</a>" ));
//			saveErrors(request,errores);

		}
		///////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_Selec_Borrar( HttpServletRequest request, ActionForm  form ) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		ActionMessages errores = new ActionMessages();
		///////////////////////////////////////////
		// Aborta si no hay filas marcadas:
		int nfilas = (pantalla.getClavesMarcadas()!=null)?pantalla.getClavesMarcadas().length:0;
		if (nfilas<1) return cargarPantalla(request,form);
		///////////////////////////////////////////
		MoAccesoBaseDatos db = new MoAccesoBaseDatos();
		String opcion = null;
		for (int j=0;j<nfilas;j++) {
			opcion = pantalla.getClavesMarcadas()[j];
			//////////////////////////////
			// Rescato la clave concatenada:
			MoBean key = new MoBean();
			String k;
	k = opcion; key.setMo_id_modo( k );
			///////////////////////////////////
			try {
				db.mo_dltObj( new Subrutinas().getBDConexion(request), key );
			} catch (StExcepcion ex) {
				errores.add("error", new ActionMessage( "errors.detail", ex.getMessage() ));
			}
			//////////////////////////////
		}
//		errores.add("error", new ActionMessage( "errors.detail", nfilas + " registros procesados." ));
//		if (errores.size()>0) saveErrors(request,errores);
		pantalla.setClavesMarcadas(null);
		///////////////////////////////////////////
		resultado = cargarPantalla(request,form);
		return resultado;
	}

	private String opcion_Selec_ChgColectivo(HttpServletRequest request, ActionForm form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		ActionMessages errores = new ActionMessages();
		String accion = pantalla.getOpcionPantalla();
		///////////////////////////////////////////
		// Aborta si no hay filas marcadas:
		int nfilas = (pantalla.getClavesMarcadas()!=null)?pantalla.getClavesMarcadas().length:0;
		if (nfilas<1) return cargarPantalla(request,form);
		///////////////////////////////////////////
		// Actualiza la extensi�n (no el original de Distrialia)
		com.cp.FuentesDeDatos.BDConexion database = new Subrutinas().getBDConexion(request);
		MoAccesoBaseDatos db = new MoAccesoBaseDatos();
		MoBean reg = null;
		String opcion = null;
		for (int j=0;j<nfilas;j++) {
			opcion = pantalla.getClavesMarcadas()[j];
			//////////////////////////////
			// Rescato la clave concatenada:
			MoBean key = new MoBean();
			String k;
	k = opcion; key.setMo_id_modo( k );
			///////////////////////////////////
			try {
				reg = db.mo_getRcd( database, key );
				if ( reg != null) {
					///////////////////////////////////
					// Aplico el nuevo valor al registro:
		if ( accion.trim().equalsIgnoreCase("colectivo_colectivo") ) { reg.setMo_colectivo( pantalla.getMo_colectivo() ); }
		if ( accion.trim().equalsIgnoreCase("colectivo_mo_nombre") ) { reg.setMo_mo_nombre( pantalla.getMo_mo_nombre() ); }
		if ( accion.trim().equalsIgnoreCase("colectivo_json") ) { reg.setMo_json( pantalla.getMo_json() ); }
					///////////////////////////////////
					db.mo_chgObj( database, reg );
				}
			} catch (StExcepcion ex) {
				errores.add("error", new ActionMessage( "errors.detail", ex.getMessage() ));
			}
			//////////////////////////////
		}
//		errores.add("error", new ActionMessage( "errors.detail", nfilas + " registros procesados." ));
//		if (errores.size()>0) saveErrors(request,errores);
		pantalla.setClavesMarcadas(null);
		///////////////////////////////////////////
		resultado = cargarPantalla(request,form);
		return resultado;
	}
	
	private void inicializarPantalla( HttpServletRequest request, ActionForm  form ) {
		MoRCD_AF pantalla = (MoRCD_AF)form;
		//////////////////////////////////
		
		// POP SuperFiltros en SESSION:
		if ( request.getSession().getAttribute("moFilaInicioGrid") != null && pantalla.getFilaInicioGrid() == 0 )
			pantalla.setFilaInicioGrid( ((java.lang.Integer) request.getSession().getAttribute("moFilaInicioGrid")).intValue() );
		
		if ( request.getSession().getAttribute("FilasGrid") != null && pantalla.getFilasGrid() == 0 )
			pantalla.setFilasGrid( ((java.lang.Integer) request.getSession().getAttribute("FilasGrid")).intValue() );
		
		// POP Filtros en SESSION:
		// Qui�n manda m�s, la pantalla o el filtro?
		MoBeanFiltro filtroOld = (MoBeanFiltro) request.getSession().getAttribute("moFiltro");
		if ( filtroOld != null ) { pantalla.setMo_filtro( (MoBeanFiltro) filtroOld.coalesce(pantalla.getMo_filtro(),filtroOld) ); }
		
		// Inicializar filtros:
		
		// PUSH SuperFiltros en SESSION:
		request.getSession().setAttribute("moFilaInicioGrid", new java.lang.Integer( pantalla.getFilaInicioGrid()) );
		request.getSession().setAttribute("FilasGrid", new java.lang.Integer( pantalla.getFilasGrid()) );
		
		// PUSH Filtros en SESSION:
		request.getSession().setAttribute("moFiltro",pantalla.getMo_filtro());
		
		// Mover par�metros (y sus virtuales y tal) al control de la pantalla:
		
		/////////////////////
		
		/////////////////////////////////////////////////
		if (  request.getSession().getAttribute("lstLineasPantalla")==null )  {
			// Bean en contexto de session
			request.getSession().setAttribute("lstLineasPantalla", Subrutinas.cargarCombo_FilasGrid() );
		}

		
		/////////////////////////////////////////////////
		
		//////////////////////////////////
	}

	private void aplicarParametrosDeEntrada(HttpServletRequest request, ActionForm form ) {
		MoRCD_AF pantalla = (MoRCD_AF)form;
		//////////////////////////////////
		// Restrictores y mapeos con destino campos de pantalla:
		MoBean rcd_Mo = (MoBean) request.getAttribute("rcd_Mo");
		if ( rcd_Mo != null ) {
			// Ha sido llamado desde otro programa:
			;
		} else {
			// Recarga de s� misma...verificar si valores legales en campos importantes u obligatorios:
			// if ( pantalla.getMo_rst0() == null || pantalla.getMo_rst0().trim().length() < 1 ) {
			//	 pantalla.setMo_rst0( "ValorDeProteccion" );
			// }
			;
		}
		///////////////////////////////////////////
		// Tratar par�metros extra recibidos:

		///////////////////////////////////////////
	}
	
	private int persistirPosiblesCambios(HttpServletRequest request, ActionForm  form ) {
		int resultado = 0;
		MoRCD_AF pantalla = (MoRCD_AF)form;
		//////////////////////////////////
		if ( pantalla != null && pantalla.getGrid() != null ) {
			MoAccesoBaseDatos db = new MoAccesoBaseDatos();
			MoBean key = null;
			MoBean reg = null;
			// Se regraba cada fila del grid marcada en campo "chg":
			for (int i=0;i<pantalla.getGrid().length;i++) {
				key = ((MoBean)(pantalla.getGrid()[i]));
				if ( key.getChg() != null && key.getChg().trim().length() > 0 ) {
					try {
						// Cargo clave entidad:
						String opcion = key.getChg().trim();
						String k;
	k = opcion; key.setMo_id_modo( k );
						reg = db.mo_getRcd( new Subrutinas().getBDConexion(request), key );
						if (reg!=null) {
							// Campo(s) a reescribir:
							// reg.setMo_Campo( ((MoBean)(pantalla.getGrid()[i])).getMo_Campo() );
							// ...meter a manita cada campo...y activar este chgObj:
							// db.mo_chgObj(new Subrutinas().getBDConexion(request),reg);
							resultado++;
							}
					} catch (StExcepcion ex) {;}
				}
			}
		}
		//////////////////////////////////
		return resultado;
	}
}
