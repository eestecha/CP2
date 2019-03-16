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
import com.cp.mo_modo.db.MoAccesoBaseDatos;
import com.cp.mo_modo.forms.MoRCD_AF;

import net.sf.json.JSONObject;

public class MoADDRCD_A { //extends Action {
	
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
		// Seguridad de acceso:
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
		if ( opcion == null )
			resultado = this.cargarPantalla( request, pantalla );
		else {
			if ( opcion.trim().length()==0 ) {
				resultado = this.cargarPantalla( request, pantalla );
			} else if ( opcion.trim().equalsIgnoreCase("LeerReg") ) {
				resultado = this.cargarPantalla( request, pantalla );
			} else if ( opcion.trim().equalsIgnoreCase("NuevoReg") ) {
				resultado = opcion_NuevoReg(request,form);
			} else if ( opcion.trim().equalsIgnoreCase("Cerrar") ) {
				resultado = opcion_Cerrar(request,form);
			} else if (opcion.trim().equalsIgnoreCase("retornoSelect")) {
				recalcularVirtuales(request, pantalla);
			} else {
				resultado = this.cargarPantalla( request, pantalla );
			}
		}
		if (resultado.equalsIgnoreCase("NOVALE"))
			resultado = this.cargarPantalla( request, pantalla );
		
		pantalla.setOpcionPantalla("");

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
	
	private String cargarPantalla(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		resultado = CamposCalculados(request,pantalla);
		///////////////////////////////////////////
		ConfigPantalla cfg = new ConfigPantalla();
		cfg.setNombrePantalla( (this.getClass().toString()).substring((this.getClass().toString()).lastIndexOf(".")+1) );
		cfg.setTituloPantalla("modo: crear");
		// Hace falta para el t�tulo y paginado:
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
	
	private String opcion_NuevoReg(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		resultado = this.chkPantalla( request, pantalla );
		// Altero imagen WRK en disco:
		if (resultado.equalsIgnoreCase("OK"))
			resultado = this.crtRcd( request, pantalla );
		if (resultado.equalsIgnoreCase("OK"))
			resultado = "CERRAR";
		///////////////////////////////////////////
		return resultado;
	}
	
	private String opcion_Cerrar(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		resultado = "CERRAR";
		///////////////////////////////////////////
		return resultado;
	}

	private void recalcularVirtuales(HttpServletRequest request, ActionForm form) {
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		Subrutinas subrutinas = new Subrutinas();
		///////////////////////////////////////////
		// POR EJEMPLO: Provincia
		//pantalla.setAc_DESC( subrutinas.rtvProvincia( request, pantalla.getAc_KEY() ) );

		

		///////////////////////////////////////////
	}
	
	private String CamposCalculados(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		// Inicializar campos:
		
		

		///////////////////////////////////////////
		// Campos deducidos:
		///////////////////////////////////////////

		
		///////////////////////////////////////////
		return resultado;
	}
	
	private String chkPantalla(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		///////////////////////////////////////////
		CamposCalculados(request,pantalla);
		///////////////////////////////////////////
		ActionMessages errores = new ActionMessages();
		// Claves obligatorias:
		if (
		   pantalla.getMo_id_modo() == null || pantalla.getMo_id_modo().trim().length() < 1
				) {
			resultado = "NOVALE";
			errores.add("error", new ActionMessage( "errors.detail", "Valores para CLAVE obligatorios." ));
		}

		///////////////////////////////////////////

		
	
		///////////////////////////////////////////
//		if ( errores.size() > 0 )
//			saveErrors(request,errores);
		///////////////////////////////////////////
		return resultado;
	}
	
	private String crtRcd(HttpServletRequest request, ActionForm  form) {
		String resultado = "OK";
		///////////////////////////////////////////
		MoRCD_AF pantalla = (MoRCD_AF)form;
		MoBean registro = null;
		if (pantalla != null ) {
			registro = new MoBean();
			
			pantalla.copyTo( registro );
			
			MoAccesoBaseDatos db = new MoAccesoBaseDatos();
			try {
				db.mo_crtObj( new Subrutinas().getBDConexion(request), registro );
			} catch (StExcepcion ex) {
				resultado = "NOVALE";
				ActionMessages errores = new ActionMessages();
				errores.add("error", new ActionMessage( "errors.detail", ex.getMessage() ));
//				saveErrors(request,errores);
			}
		}
		///////////////////////////////////////////
		return resultado;
	}
	
}
