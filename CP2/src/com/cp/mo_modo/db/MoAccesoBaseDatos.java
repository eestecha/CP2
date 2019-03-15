package com.cp.mo_modo.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cp.FuentesDeDatos.BDConexion;
import com.cp._comun.ConfigPantalla;
import com.cp._comun.RstAplicar;
import com.cp._comun.StBean;
import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;
import com.cp._comun._K;
import com.cp.mo_modo.bean.MoBean;
import com.cp.mo_modo.bean.MoBeanFiltro;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MoAccesoBaseDatos {
    public String tabla   = "t_mo_modo";
    public String lf_UPD  = "t_mo_modo";
    public String lf_RTV  = "v_mo_rtv_modo";

    ////////////////////////////////////////////////////////////////////
    // Opcionalmente se pueden conectar las funciones CRUD+getRcd+getSeq
    // a un "Sistema Externo", convirtiendo sistem�ticamente
    // sus par�metros en ficheros cada uno con su valor respectivo.
    // ACTIVAR para 'Sistema Externo' con paso de par�metros por FileSystem:
    protected final boolean isParmViaFS = false;
    ////////////////////////////////////////////////////////////////////
    protected File fo;
    protected BufferedWriter dout;
    public MoAccesoBaseDatos() {fo=null;dout=null;}
/////////////////////////////////////////////////
// mo_modo:
/////////////////////////////////////////////////
    protected void callSistemaExterno( final String idOp ) throws StExcepcion {
    	// System.out.println("\r\n*** callSistemaExterno( "+idOp+" )");

    	String[] params = new String [4];
    	params[0] = _K.caminoExecExterno + _K.ejecutableExterno;
    	params[1] = _K.caminoExecExterno;
    	params[2] = idOp;
    	params[3] = _K.unidadIntercambio;
    	
    	// String salidaTerminal = 
    			Subrutinas.run_comando_sincro(params);
    	// System.out.println( salidaTerminal );
    }
    protected void runSql(BDConexion dataBase, String sql) throws StExcepcion {
        //////////////////////////////////////////////
        try {
            if (dataBase==null) dataBase = new BDConexion();
            dataBase.executeUpdate(sql);
        } catch (StExcepcion ex) {
            throw ex;
        }
        //////////////////////////////////////////////
    }
    public void mo_crtObj(BDConexion bd, MoBean registro) throws StExcepcion {

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Variante para versi�n de paso de par�metros por FileSystem:
        if (isParmViaFS) {
	    	final String idOp = Integer.toHexString(hashCode()).toUpperCase() + "_mo_CRT";
	        //////////////////////////////////////////////
	        // 1.grabar par�metros, 
	    	mo_putParFS_bean( idOp, registro );
	        // 2.Invocar Sistema Externo S�NCRONO!
	        callSistemaExterno( idOp );
	        // 3.Leer resultados
	    	mo_getParFS_RetCode(idOp);
	    	return;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
    	
        //////////////////////////////////////////////
        if (bd==null) bd = new BDConexion();
        //////////////////////////////////////////////
        String sql =
                "INSERT INTO " + Subrutinas.getG_DB_LIBDAT(bd.getCurrentDb()) + "."  + this.lf_UPD + " " +
                "( " + 
		"  id_modo" + // id_modo
		", colectivo" + // colectivo
		", mo_nombre" + // mo_nombre
		", json" + // json
                "  ) VALUES ( " + 
		"  '"  + registro.getMo_id_modo() + "'" + // id_modo
		", '"  + registro.getMo_colectivo() + "'" + // colectivo
		", '"  + registro.getMo_mo_nombre() + "'" + // mo_nombre
		", '"  + registro.getMo_json() + "'" + // json 
                ")"
                ;
        //////////////////////////////////////////////
        runSql(bd,sql);
        //////////////////////////////////////////////
    }
    public void mo_chgObj(BDConexion bd, MoBean registro) throws StExcepcion {

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Variante para versi�n de paso de par�metros por FileSystem:
        if (isParmViaFS) {
	    	final String idOp = Integer.toHexString(hashCode()).toUpperCase() + "_mo_CHG";
	        //////////////////////////////////////////////
	        // 1.grabar par�metros, 
	    	mo_putParFS_bean( idOp, registro );
	        // 2.Invocar Sistema Externo S�NCRONO!
	        callSistemaExterno( idOp );
	        // 3.Leer resultados
	    	mo_getParFS_RetCode(idOp);
	    	return;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
    	
        //////////////////////////////////////////////
        if (bd==null) bd = new BDConexion();
        //////////////////////////////////////////////
        String sql =
                "UPDATE " + Subrutinas.getG_DB_LIBDAT(bd.getCurrentDb()) + "."  + this.lf_UPD + " " +
                "   SET " + 
		"  id_modo = '"  + registro.getMo_id_modo() + "'" + // id_modo
		", colectivo = '"  + registro.getMo_colectivo() + "'" + // colectivo
		", mo_nombre = '"  + registro.getMo_mo_nombre() + "'" + // mo_nombre
		", json = '"  + registro.getMo_json() + "'" + // json
                " WHERE " + 
		"  id_modo = '" + registro.getMo_id_modo() + "'" + // id_modo
                ""
                ;
        //////////////////////////////////////////////
        runSql(bd,sql);
        //////////////////////////////////////////////
    }
    public void mo_dltObj(BDConexion bd, MoBean registro) throws StExcepcion {

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Variante para versi�n de paso de par�metros por FileSystem:
        if (isParmViaFS) {
	    	final String idOp = Integer.toHexString(hashCode()).toUpperCase() + "_mo_DLT";
	        //////////////////////////////////////////////
	        // 1.grabar par�metros, 
	    	mo_putParFS_bean( idOp, registro );
	        // 2.Invocar Sistema Externo S�NCRONO!
	        callSistemaExterno( idOp );
	        // 3.Leer resultados
	    	mo_getParFS_RetCode(idOp);
	    	return;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
    	
        //////////////////////////////////////////////
        if (bd==null) bd = new BDConexion();
        //////////////////////////////////////////////
        String sql =
                "DELETE " +
                " FROM " + Subrutinas.getG_DB_LIBDAT(bd.getCurrentDb()) + "."  + this.lf_UPD + " " +
                " WHERE " + 
		"  id_modo = '" + registro.getMo_id_modo() + "'" + // id_modo
                ""
                ;
        //////////////////////////////////////////////
        runSql(bd,sql);
        //////////////////////////////////////////////
    }
    public MoBean   mo_getRcd(BDConexion dataBase, MoBean registro) throws StExcepcion {

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Variante para versi�n de paso de par�metros por FileSystem:
        if (isParmViaFS) {
	    	final String idOp = Integer.toHexString(hashCode()).toUpperCase() + "_mo_GET";
	        //////////////////////////////////////////////
	        // 1.grabar par�metros, 
	    	mo_putParFS_bean( idOp, registro );
	        // 2.Invocar Sistema Externo S�NCRONO!
	        callSistemaExterno( idOp );
	        // 3.Leer resultados
	    	return mo_getParFS_GET(idOp);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
    	
        //////////////////////////////////////////////
        if (dataBase==null) dataBase= new BDConexion();
        //////////////////////////////////////////////
        String sql =
                "SELECT A.*" +
                " FROM " + Subrutinas.getG_DB_LIBDAT(dataBase.getCurrentDb()) + "."  + this.lf_RTV + " A" +
                " WHERE " + 
		"  id_modo = '" + registro.getMo_id_modo() + "'" + // id_modo
                ""
                ;
        ResultSet rs = null;
        MoBean regRead = null;
        //////////////////////////////////////////////
        //if (dataBase==null) dataBase = new BDConexion();
        try {
            rs = dataBase.executeQuery(sql);
            if (rs.next()){
                regRead = new MoBean();
                
		regRead.setMo_id_modo( rs.getString("id_modo") ); regRead.setMo_id_modo( (regRead.getMo_id_modo() == null)?"":regRead.getMo_id_modo().trim() ); // id_modo
		regRead.setMo_colectivo( rs.getString("colectivo") ); regRead.setMo_colectivo( (regRead.getMo_colectivo() == null)?"":regRead.getMo_colectivo().trim() ); // colectivo
		regRead.setMo_mo_nombre( rs.getString("mo_nombre") ); regRead.setMo_mo_nombre( (regRead.getMo_mo_nombre() == null)?"":regRead.getMo_mo_nombre().trim() ); // mo_nombre
		regRead.setMo_json( rs.getString("json") ); regRead.setMo_json( (regRead.getMo_json() == null)?"":regRead.getMo_json().trim() ); // json
            }
        } catch (SQLException ex0) {
            throw new StExcepcion(ex0.getMessage());
        } catch (StExcepcion ex1) {
            throw new StExcepcion(ex1.getMessage());
        } finally {
            try {
                if ( rs != null ) { BDConexion.rsClose( dataBase, rs ); }
            } catch (SQLException ex2) {
                throw new StExcepcion(ex2.getMessage());
            }
        }
        //////////////////////////////////////////////
        
        return regRead;
    }
    public MoBean[] mo_getSeq(BDConexion dataBase, ConfigPantalla extCfg, MoBeanFiltro rst ) throws StExcepcion {
        MoBean[] filasRecuperadas = null;
        ///////////////////////////////////////////////////////
        ConfigPantalla cfg = (extCfg!=null)?extCfg:new ConfigPantalla();
        if ( cfg.isExportar() ) {
            cfg.setFilaInicioGrid(1);
            cfg.setFilasGrid(Integer.MAX_VALUE);
            cfg.setFilasTotales(0);
            getSeq_Sub_ExportIni( cfg.getTituloPantalla() );
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Variante para versi�n de paso de par�metros por FileSystem:
        if (isParmViaFS) {
	    	final String idOp = Integer.toHexString(hashCode()).toUpperCase() + "_mo_GETSEQ";
	        //////////////////////////////////////////////
	        // 1.grabar par�metros, 
            mo_putParFS_GETSEQ( idOp, cfg, rst );
	        // 2.Invocar Sistema Externo S�NCRONO!
	        callSistemaExterno( idOp );
	        // 3.Leer resultados
            return mo_getParFS_GETSEQ( idOp, cfg );
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        
        //////////////////////////////////////////////
        if (dataBase==null) dataBase= new BDConexion();
        ///////////////////////////////////////////////////////
        String sql =
                "SELECT A.*" +
                " FROM " + Subrutinas.getG_DB_LIBDAT(dataBase.getCurrentDb()) + "."  + this.lf_RTV + " A"
                ;
        String sqlWhere = "";
        ///////////////////////////////////////////////////////
        // Filtros de la lista:
        RstAplicar fltOper = new RstAplicar(dataBase.getRwUpperCase(),dataBase.getRwLike(),dataBase.getRwAnyString());
	
	sqlWhere = fltOper.getCHAR_LIKE(rst.getMo_id_modo(),"id_modo",sqlWhere);   // id_modo
	sqlWhere = fltOper.getCHAR_LIKE(rst.getMo_colectivo(),"colectivo",sqlWhere);   // colectivo
	sqlWhere = fltOper.getCHAR_LIKE(rst.getMo_mo_nombre(),"mo_nombre",sqlWhere);   // mo_nombre
	sqlWhere = fltOper.getCHAR_LIKE(rst.getMo_json(),"json",sqlWhere);   // json
        //////////////////////////////////////////////////////
        
	   

        //////////////////////////////////////////////////////
        sql += sqlWhere;
        // Campos de ordenaci�n:
        sql += " ORDER BY \"id_modo\" ASC";
        //////////////////////////////////////////////////////
        ResultSet rs = null;
        MoBean regRead = null;
        ArrayList<MoBean> arrayTmp = new ArrayList<MoBean>();
        //////////////////////////////////////////////
        //if (dataBase==null) dataBase = new BDConexion();
        try {
            ///////////////////////////////////////
            // Configuraci�n del DSPFIL (NumFilas, NumPantallas...)
            if (cfg != null) {
                String sqlCount = "SELECT COUNT(*) AS nFilas FROM " + Subrutinas.getG_DB_LIBDAT(dataBase.getCurrentDb()) + "."  + lf_RTV + " A";
                sqlCount += sqlWhere;
                rs = dataBase.executeQuery(sqlCount);
                cfg.setFilasTotales(0);
                if ( rs.next() ) cfg.setFilasTotales( rs.getInt("nFilas") );
                if ( rs != null ) { BDConexion.rsClose( dataBase, rs ); }
                ///////////////////////////////////////
                if ( cfg.isExportar() ) {
                    if ( cfg.getFilasTotales() > 5000 ) {
                        getSeq_Sub_ExportFin();
                        throw new StExcepcion("Se permiten exportar hasta 5000 filas.\r\nPor favor aplique una selecci�n mas restrictiva.");
                    }
                }
            }
            ///////////////////////////////////////

			// C�digo para postgres
            sql += " LIMIT "  + cfg.getFilasGrid();
            sql += " OFFSET " + (cfg.getFilaInicioGrid()-1);
            rs = dataBase.executeQuery(sql);
            if ( rs != null ) {
                int filas = 0;
                  if ( rs.next() ) {
                    do {
                        regRead = new MoBean();
                        
		regRead.setMo_id_modo( rs.getString("id_modo") ); regRead.setMo_id_modo( (regRead.getMo_id_modo() == null)?"":regRead.getMo_id_modo().trim() ); // id_modo
		regRead.setMo_colectivo( rs.getString("colectivo") ); regRead.setMo_colectivo( (regRead.getMo_colectivo() == null)?"":regRead.getMo_colectivo().trim() ); // colectivo
		regRead.setMo_mo_nombre( rs.getString("mo_nombre") ); regRead.setMo_mo_nombre( (regRead.getMo_mo_nombre() == null)?"":regRead.getMo_mo_nombre().trim() ); // mo_nombre
		regRead.setMo_json( rs.getString("json") ); regRead.setMo_json( (regRead.getMo_json() == null)?"":regRead.getMo_json().trim() ); // json
                        
                        if ( cfg.isExportar() ) getSeq_Sub_ExportMid( regRead );
                        else                    arrayTmp.add( regRead );

                        filas++;
                    } while( rs.next() && filas < (  (cfg!=null)?cfg.getFilasGrid():(new ConfigPantalla()).getFilasGrid() ) );
                }
            }
        } catch (SQLException ex0) {
            throw new StExcepcion(ex0.getMessage());
        } catch (StExcepcion ex1) {
            throw new StExcepcion(ex1.getMessage());
        } finally {
            try {
                if ( rs != null ) { BDConexion.rsClose( dataBase, rs ); }
            } catch (SQLException ex2) {
                throw new StExcepcion(ex2.getMessage());
            }
        }
        //////////////////////////////////////////////
        if ( cfg.isExportar() ) {
            getSeq_Sub_ExportFin();
        }
        //////////////////////////////////////////////
        filasRecuperadas = new MoBean[arrayTmp.size()];
        filasRecuperadas = arrayTmp.toArray(filasRecuperadas);
        return filasRecuperadas;
    }
    protected void getSeq_Sub_ExportIni( String NombreArchivo ) throws StExcepcion {
        /////////////////////////////
        // Nombre completo del archivo viene por par�metros.
        /////////////////////////////
        fo = new File( NombreArchivo );
        try {
        	
//        	int i = fo.getAbsolutePath().lastIndexOf(File.separator);
//        	if ( i > -1 ) {
//        		try { 
//        			new File(fo.getAbsolutePath().substring(0, i)).mkdirs();
//				} catch (Exception e) {;}
//        	}
        	
            dout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fo)));
            if (dout!=null) {
				String s = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
				s += "\r\n<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\"><title>"+fo.getName()+"</title></head><body><table>\r\n";
				s += "<tr>";
				s += "<td><strong style='color:darkblue;'>" + "id_modo" + "</strong></td>";  // id_modo
				s += "<td><strong style='color:darkblue;'>" + "colectivo" + "</strong></td>";  // colectivo
				s += "<td><strong style='color:darkblue;'>" + "mo_nombre" + "</strong></td>";  // mo_nombre
				s += "<td><strong style='color:darkblue;'>" + "json" + "</strong></td>";  // json
				s += "</tr>\r\n";
                dout.write(s);
            }
        } catch (FileNotFoundException ex1) {
            throw new StExcepcion(ex1.getMessage());
        } catch (IOException ex2) {
            throw new StExcepcion(ex2.getMessage());
        }
        /////////////////////////////
    }
    protected void getSeq_Sub_ExportMid(MoBean registro) throws StExcepcion {
        String s = "";
		String tmp = "";
		
		s += "<tr>";
				tmp = registro.getMo_id_modo();
				try {tmp = new String( tmp.getBytes(), "iso-8859-1" );} catch (UnsupportedEncodingException ex) {;}
				s += "<td>" + tmp + "</td>";  // id_modo
				tmp = registro.getMo_colectivo();
				try {tmp = new String( tmp.getBytes(), "iso-8859-1" );} catch (UnsupportedEncodingException ex) {;}
				s += "<td>" + tmp + "</td>";  // colectivo
				tmp = registro.getMo_mo_nombre();
				try {tmp = new String( tmp.getBytes(), "iso-8859-1" );} catch (UnsupportedEncodingException ex) {;}
				s += "<td>" + tmp + "</td>";  // mo_nombre
				tmp = registro.getMo_json();
				try {tmp = new String( tmp.getBytes(), "iso-8859-1" );} catch (UnsupportedEncodingException ex) {;}
				s += "<td>" + tmp + "</td>";  // json
		s += "</tr>\r\n";

        // Grabar en el archivo de salida:
        if (fo==null) return;
        if (dout==null) return;
        try {
            dout.write(s);
        } catch (FileNotFoundException ex1) {
            throw new StExcepcion(ex1.getMessage());
        } catch (IOException ex2) {
            throw new StExcepcion(ex2.getMessage());
        }
    }
    protected void getSeq_Sub_ExportFin() throws StExcepcion {
        try {
            dout.write("</table></body></html>");
            dout.close();
        } catch (IOException ex) {
            throw new StExcepcion(ex.getMessage());
        }
    }
/////////////////////////////////////////////////
    protected void     mo_putParFS_bean( final String idOp, MoBean par ) throws StExcepcion {
    	StringBuffer log = new StringBuffer();

    	new File( _K.caminoSalida  ).mkdirs();

    	// Propagar mis par�metros de ENTRADA:
    	final String pPar = _K.caminoSalida  + idOp + "_par" + _K.extFicParm;

    	// 1d3.Generar archivos de par�metros:
    	Subrutinas.grabFile(log, pPar, par.serializar().getBytes() ); if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}
//    	  par.deserializar( Subrutinas.readFile(log, pPar ) );	// TEST

    }
    protected void     mo_putParFS_GETSEQ( final String idOp, ConfigPantalla cfg, MoBeanFiltro rst ) throws StExcepcion {
    	StringBuffer log = new StringBuffer();

    	new File( _K.caminoSalida  ).mkdirs();

    	// Propagar mis par�metros de ENTRADA:
    	final String pCfg = _K.caminoSalida  + idOp + "_cfg" + _K.extFicParm;
    	final String pRst = _K.caminoSalida  + idOp + "_rst" + _K.extFicParm;

    	// 1d3.Generar archivos de par�metros:
    	Subrutinas.grabFile(log, pCfg, cfg.serializar().getBytes() ); if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}
    	Subrutinas.grabFile(log, pRst, rst.serializar().getBytes() ); if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}

//        cfg.deserializar( Subrutinas.readFile(log, pCfg ) );	// TEST
//        rst.deserializar( Subrutinas.readFile(log, pRst ) );	// TEST

    }

    protected void     mo_getParFS_RetCode( final String idOp ) throws StExcepcion {
        StringBuffer log = new StringBuffer();

        new File( _K.caminoEntrada ).mkdirs();
        
        // Recoger mis par�metros de SALIDA:
        final String pRC = _K.caminoEntrada + idOp + "_RC" + _K.extFicParm;
        
        // 3d3.Leer resultados
        String rc = Subrutinas.readFile(log, pRC ); if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}

        try { new File(pRC).delete(); } catch (Exception e) {;} // Sistema LECTOR, SUPRIME lo leido

        if ( rc == null ) { throw new StExcepcion("El sistema externo no retorna valor."); }
        if ( rc != null && rc.trim().length() > 0 ) { throw new StExcepcion(rc); }
        
    }
    protected MoBean   mo_getParFS_GET( final String idOp ) throws StExcepcion {
    	
    	MoBean reg = null;
    	
        StringBuffer log = new StringBuffer();

        new File( _K.caminoEntrada ).mkdirs();
        
        // Recoger mis par�metros de SALIDA:
        final String pResultados = _K.caminoEntrada + idOp + _K.extFicParm;
        
        // 3d3.Leer resultados
        mo_getParFS_RetCode( idOp );
        String rg = Subrutinas.readFile(log, pResultados );	 if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}

        try { new File(pResultados).delete(); } catch (Exception e) {;} // Sistema LECTOR, SUPRIME lo leido
        
        if ( rg != null && rg.trim().length() > 0 ) { 
            reg = new MoBean();
            reg.deserializar(rg);
        }

		return reg;
    }
    protected MoBean[] mo_getParFS_GETSEQ( final String idOp, ConfigPantalla cfg ) throws StExcepcion {

    	MoBean[] resultado = null;

        StringBuffer log = new StringBuffer();

        new File( _K.caminoEntrada ).mkdirs();

        // Recoger mis par�metros de SALIDA:
        final String pResultados = _K.caminoEntrada + idOp          + _K.extFicParm;
        final String pConfigPant = _K.caminoEntrada + idOp + "_cfg" + _K.extFicParm;

        // 3d3.Leer resultados
        mo_getParFS_RetCode( idOp );
        String regs = Subrutinas.readFile(log, pResultados ); if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}
        String sCfg = Subrutinas.readFile(log, pConfigPant ); //if(log.toString().trim().length()>0){throw new StExcepcion(log.toString());}

        try { new File(pResultados).delete(); } catch (Exception e) {;} // Sistema LECTOR, SUPRIME lo leido
        try { new File(pConfigPant).delete(); } catch (Exception e) {;} // Sistema LECTOR, SUPRIME lo leido

        if ( regs != null && regs.trim().length() > 0 ) {
        	String[] lstRegs = regs.split( _K.sepReg );
        	resultado = new MoBean[lstRegs.length];
        	int i = 0;
        	for ( String e : lstRegs ) {
            	resultado[i] = new MoBean();
                if ( e != null && e.trim().length() > 0 ) { 
                	resultado[i].deserializar( e );
                }
                i++;
        	}
        } else {
        	resultado = new MoBean[0];
        }

		if ( sCfg != null && sCfg.trim().length() > 0 ) {
			cfg.deserializar(sCfg);
		}

        return resultado;
    }
/////////////////////////////////////////////////
	public JSONObject beanArray2json( MoBean[] lista ) {
		JSONObject jsonObject = new JSONObject(); 
		JSONArray jsonArray = new JSONArray();
		//////////////////////
		if ( lista != null ) {
			for ( StBean item : lista ) {
				jsonArray.add( item.toString() );
			}
		}
		jsonObject.put("registros", jsonArray);
		return jsonObject;
	}
	public MoBean[] json2beanArray(JSONObject jsonObject) {
		MoBean[] resultado = null;

		ArrayList<MoBean> arrayTmp = new ArrayList<MoBean>();
		JSONArray jsonReg = null;

		if ( jsonObject != null ) {
			int i = 1;
			do {
				jsonReg = null;
				try {
					jsonReg = jsonObject.getJSONArray( "r" + (i++) );
					MoBean registro = new MoBean();
					
				registro.setMo_id_modo( jsonReg.getString(0) );	// id_modo
				registro.setMo_colectivo( jsonReg.getString(1) );	// colectivo
				registro.setMo_mo_nombre( jsonReg.getString(2) );	// mo_nombre
				registro.setMo_json( jsonReg.getString(3) );	// json
					
					arrayTmp.add(registro);
				} catch (Exception e) {;}
			} while( jsonReg != null );
		}

		//////////////////////////////////////////////
		resultado = new MoBean[arrayTmp.size()];
		resultado = arrayTmp.toArray(resultado);
		arrayTmp.clear();

		return resultado;

	}
/////////////////////////////////////////////////
}
