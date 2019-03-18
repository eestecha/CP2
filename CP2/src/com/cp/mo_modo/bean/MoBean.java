package com.cp.mo_modo.bean;

import com.cp._comun.StBean;
import com.cp._comun._K;

public class MoBean extends StBean {

	private static final long serialVersionUID = 2970785043558791286L;

	public String mo_id_modo; // id_modo
	public String mo_colectivo; // colectivo
	public String mo_mo_nombre; // mo_nombre
	public String mo_json; // json
    
    public MoBean() {
        super();
        inicializar();
    }
    
    // Constructor que deja 'null' en todos sus miembros
    public MoBean(Object nulo) { super(); }

    public void inicializar() {
	this.setMo_id_modo( "" ); // id_modo
	this.setMo_colectivo( "" ); // colectivo
	this.setMo_mo_nombre( "" ); // mo_nombre
	this.setMo_json( "" ); // json
    } 
 /*
    public void copyTo(StBean beanDestino) {
        MoBean Destino = (MoBean)beanDestino;

	Destino.setMo_id_modo( getMo_id_modo() ); // id_modo
	Destino.setMo_colectivo( getMo_colectivo() ); // colectivo
	Destino.setMo_mo_nombre( getMo_mo_nombre() ); // mo_nombre
	Destino.setMo_json( getMo_json() ); // json
    }
    
    public void copyFrom(StBean beanOrigen) {
        MoBean Origen = (MoBean)beanOrigen;

	setMo_id_modo( Origen.getMo_id_modo() ); // id_modo
	setMo_colectivo( Origen.getMo_colectivo() ); // colectivo
	setMo_mo_nombre( Origen.getMo_mo_nombre() ); // mo_nombre
	setMo_json( Origen.getMo_json() ); // json
    }
*/


	/** Get id_modo*/
	public String getMo_id_modo() {return mo_id_modo;}
	/** Set id_modo*/
	public void setMo_id_modo(String mo_id_modo) {this.mo_id_modo = mo_id_modo;}

	/** Get colectivo*/
	public String getMo_colectivo() {return mo_colectivo;}
	/** Set colectivo*/
	public void setMo_colectivo(String mo_colectivo) {this.mo_colectivo = mo_colectivo;}

	/** Get mo_nombre*/
	public String getMo_mo_nombre() {return mo_mo_nombre;}
	/** Set mo_nombre*/
	public void setMo_mo_nombre(String mo_mo_nombre) {this.mo_mo_nombre = mo_mo_nombre;}

	/** Get json*/
	public String getMo_json() {return mo_json;}
	/** Set json*/
	public void setMo_json(String mo_json) {this.mo_json = mo_json;}


	////////////////////////////////////////////////////////////
    public String getKey(){
		 return mo_id_modo;}

    public void setKey(String key){
            String k="";
	k = key; this.setMo_id_modo( k );
    }
	////////////////////////////////////////////////////////////
	public String serializar() {
		StringBuffer out = new StringBuffer();
		
		                         out.append( this.getMo_id_modo()==null?"":this.getMo_id_modo() ); // id_modo
		out.append( _K.sepFld ); out.append( this.getMo_colectivo()==null?"":this.getMo_colectivo() ); // colectivo
		out.append( _K.sepFld ); out.append( this.getMo_mo_nombre()==null?"":this.getMo_mo_nombre() ); // mo_nombre
		out.append( _K.sepFld ); out.append( this.getMo_json()==null?"":this.getMo_json() ); // json

		out.append( _K.sepReg );
		
		return out.toString();
	}
	public void deserializar(String in) {
		inicializar();
		if ( in != null && in.length() > 0 ) {
			
			String s = in.replaceAll( _K.sepReg, "" );
                   s =  s.replaceAll( _K.sepReg_0x0D, "" );
                   s =  s.replaceAll( _K.sepReg_0x0A, "" );
			String[] trozos = s.split( _K.sepFld );
			
			try { this.setMo_id_modo( trozos[0] ); } catch (Exception e) {;} // id_modo
			try { this.setMo_colectivo( trozos[1] ); } catch (Exception e) {;} // colectivo
			try { this.setMo_mo_nombre( trozos[2] ); } catch (Exception e) {;} // mo_nombre
			try { this.setMo_json( trozos[3] ); } catch (Exception e) {;} // json
			
		}
	}
	////////////////////////////////////////////////////////////

}
