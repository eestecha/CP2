package com.cp.mo_modo.forms;

import com.cp._comun.ActionForm;
import com.cp._comun.StBean;
import com.cp.mo_modo.bean.MoBean;
import com.cp.mo_modo.bean.MoBeanFiltro;

public class MoRCD_AF extends ActionForm {
    public static final long serialVersionUID = 1L; // Para evitar "warning: [serial] serializable class..."

    // Para multiregistro:
    public MoBeanFiltro mo_filtro;
    
    // Para SELRCD:
    public String retFormulario;
    public String retElemento;
    public String valorInicial;
    
    // Para multiregistro:
    public StBean[] grid;
    public int    filasGrid;
    public int    filaInicioGrid;
    public int    filasTotales;
    public String[] clavesMarcadas;
    // Para todas (mono y multi):
    public String opcionPantalla;
    public String opcionJSMenu;
    
    public String logon_USR;
    
    // Datos calculados, no de BD:
    
    // Formato de registro:
	public String mo_id_modo; // id_modo
	public String mo_colectivo; // colectivo
	public String mo_mo_nombre; // mo_nombre
	public String mo_json; // json
    

    public MoRCD_AF() {
	super();
        if ( mo_filtro == null ) { mo_filtro = new MoBeanFiltro(null); }
        if (this.getGrid() == null) {
            setGrid( new MoBean[50]  );
            for (int i=0; i<50; i++) {
                grid[i] = new MoBean();
            }
        }
    }
    
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
    
    public void copyFrom(MoRCD_AF beanOrigen) {
        MoRCD_AF Origen = beanOrigen;

        setMo_filtro( Origen.getMo_filtro() );

        setRetFormulario( Origen.getRetFormulario()  );
        setRetElemento( Origen.getRetElemento()  );
        setValorInicial(Origen.getValorInicial());
        
        setGrid( Origen.getGrid() );
        setClavesMarcadas( Origen.getClavesMarcadas() );
        setFilaInicioGrid( Origen.getFilaInicioGrid() );
        setFilasGrid( Origen.getFilasGrid() );
        setOpcionPantalla( Origen.getOpcionPantalla() );
        setOpcionJSMenu( Origen.getOpcionJSMenu() );

	setMo_id_modo( Origen.getMo_id_modo() ); // id_modo
	setMo_colectivo( Origen.getMo_colectivo() ); // colectivo
	setMo_mo_nombre( Origen.getMo_mo_nombre() ); // mo_nombre
	setMo_json( Origen.getMo_json() ); // json
    }
    
    public MoBeanFiltro getMo_filtro() { return mo_filtro; }
    
    public void setMo_filtro(MoBeanFiltro mo_filtro) { this.mo_filtro = mo_filtro; }

    public StBean[] getGrid() { return grid; }

    public void setGrid(StBean[] grid) { this.grid = grid; }

    public int getFilasGrid() { return filasGrid; }

    public void setFilasGrid(int filasGrid) { this.filasGrid = filasGrid; }

    public int getFilaInicioGrid() { return filaInicioGrid; }

    public void setFilaInicioGrid(int filaInicioGrid) { this.filaInicioGrid = filaInicioGrid; }

    public int getFilasTotales() { return filasTotales; }

	public void setFilasTotales(int filasTotales) { this.filasTotales = filasTotales; }

    public String[] getClavesMarcadas() { return clavesMarcadas; }

    public void setClavesMarcadas(String[] clavesMarcadas) { this.clavesMarcadas = clavesMarcadas; }

    public String getOpcionPantalla() { return opcionPantalla; }

    public void setOpcionPantalla(String opcionPantalla) { this.opcionPantalla = opcionPantalla; }

    public String getOpcionJSMenu() { return opcionJSMenu; }

    public void setOpcionJSMenu(String opcionJSMenu) { this.opcionJSMenu = opcionJSMenu; }

    public String getLogon_USR() { return logon_USR; }

    public void setLogon_USR(String logon_USR) { this.logon_USR = logon_USR; }

    public String getRetFormulario() { return retFormulario; }

    public void setRetFormulario(String retFormulario) { this.retFormulario = retFormulario; }

    public String getRetElemento() { return retElemento; }

    public void setRetElemento(String retElemento) { this.retElemento = retElemento; }

    public String getValorInicial() { return valorInicial; }

    public void setValorInicial(String valorInicial) { this.valorInicial = valorInicial; }


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

}
