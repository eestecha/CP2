package com.cp._comun;

/**
 * @author Emilio Estecha 2013
 *
 */
public class ConfigPantalla {

    private int filasTotales;
    private int filasGrid;
    private int filaInicioGrid;
    
    private boolean exportar;
    
    private String nombrePantalla;
    private String tituloPantalla;
    private String fechaHora;

    public ConfigPantalla() {
        this.inicializar(0,Subrutinas.getG_filas_DSPFIL(), 1);
    }
    public ConfigPantalla(int FxP) {
        this.inicializar(0,FxP,1);
    }
    private void inicializar(int filasTotales, int filasGrid, int filaInicioGrid){
        setFilasTotales(filasTotales);
        setFilasGrid(filasGrid);
        setFilaInicioGrid(filaInicioGrid);
        setFechaHora( Subrutinas.getFechaHumana() );
    }
	////////////////////////////////////////////////////////////
    
	public String serializar() {
		StringBuffer out = new StringBuffer();
		
		out.append( "" + this.filasTotales );
		out.append( _K.sepFld ); 
		out.append( "" + this.filasGrid );
		out.append( _K.sepFld ); 
		out.append( "" + this.filaInicioGrid );
		out.append( _K.sepFld ); 
		out.append( "" + this.exportar );
		out.append( _K.sepFld ); 
		out.append( "" + this.nombrePantalla );
		out.append( _K.sepFld ); 
		out.append( "" + this.tituloPantalla );
		out.append( _K.sepFld ); 
		out.append( "" + this.fechaHora );

		out.append( _K.sepReg );
		
		return out.toString();
	}

	public void deserializar(String in) {
		this.inicializar(0,Subrutinas.getG_filas_DSPFIL(), 1);
		
		if ( in != null && in.length() > 0 ) {
			
			String s = in.replaceAll( _K.sepReg, "" );
			String[] trozos = s.split( _K.sepFld );

			try { this.filasTotales   = Subrutinas.parse_integer( trozos[0] ); } catch (Exception e) {;}
		    try { this.filasGrid      = Subrutinas.parse_integer( trozos[1] ); } catch (Exception e) {;}
		    try { this.filaInicioGrid = Subrutinas.parse_integer( trozos[2] ); } catch (Exception e) {;}

			try { this.exportar       = "true".equalsIgnoreCase(trozos[3])?true:false; } catch (Exception e) {;}		

		    try { this.nombrePantalla = trozos[4]; } catch (Exception e) {;}
		    try { this.tituloPantalla = trozos[5]; } catch (Exception e) {;}
		    try { this.fechaHora      = trozos[6]; } catch (Exception e) {;}
			
		}
	}

	////////////////////////////////////////////////////////////
	
    public String getPaginado_innerHTML() {
        return get_pagina_HTML() + " " + get_paginado_HTML();
    }
    
    public String get_paginado_HTML() {
        
        String cadena = "";
        if ( getFilasTotales() <= getFilasGrid() ) return cadena;

//        int primerRegistro =  getFilaInicioGrid();
        int panActual  = getPaginaActual();
        int panTotales = getPaginasTotales();
        
        int panVistas = 6;
        int mitadVistas  = panVistas / 2;
        boolean marcarMenos = true;
        boolean marcarMas   = true;

        int panInicio = panActual - mitadVistas;
        if ( panInicio <= 1 ) { 
            panInicio = 1;
            marcarMenos = false;
         }

        int panFinal  = panInicio + panVistas-1;
        if ( panFinal >= panTotales ) {
            panFinal = panTotales;
            marcarMas = false;
        }
        
        if (marcarMenos) 
            cadena += "<a href='javascript:setFilaInicioGrid( 1+((getNumPagina()-1)*" + getFilasGrid() + "));'>...</a>&nbsp;";
        
        for (int i = panInicio; i <= panFinal ; i++) {
            cadena += "<a href='javascript:setFilaInicioGrid(" + (1+((i-1) * getFilasGrid() )) + ");'>";
            cadena += i;
            cadena += "</a>&nbsp;";
        }
        if (marcarMas) 
            cadena += "<a href='javascript:setFilaInicioGrid( 1+((getNumPagina()-1)*" + getFilasGrid() + "));'>...</a>&nbsp;";
        
        return "(&nbsp;" + cadena + ")";
    }
    
    public String get_pagina_HTML() {
        String cadena = "";
        cadena += "Pg: " + getPaginaActual();
        
        if ( getFilasTotales() <= getFilasGrid() ) return cadena;

        cadena += " / " + getPaginasTotales();
        return cadena;
    }
    
    public int getPaginaActual() {
        if ( getFilaInicioGrid() < 1 ) setFilaInicioGrid(1);
        return ( ( (getFilaInicioGrid()-1) / getFilasGrid()) + 1 );
    }
    
    public int getPaginasTotales() {
        double d = (double)getFilasTotales() / (double)getFilasGrid();
        if ( d > (double)(int)d ) d += 1.0;
        return ( (int)d );
    }

    ////////////////////////////////////////////////////

    public int getFilasTotales() {
        return filasTotales;
    }

    public void setFilasTotales(int filasTotales) {
        this.filasTotales = filasTotales;
    }

    public int getFilasGrid() {
        return filasGrid;
    }

    public void setFilasGrid(int filasGrid) {
        this.filasGrid = filasGrid;
    }

    public int getFilaInicioGrid() {
        return filaInicioGrid;
    }

    public void setFilaInicioGrid(int filaInicioGrid) {
        this.filaInicioGrid = filaInicioGrid;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNombrePantalla() {
        return nombrePantalla;
    }

    public void setNombrePantalla(String nombrePantalla) {
        this.nombrePantalla = nombrePantalla;
    }

    public String getTituloPantalla() {
        return tituloPantalla;
    }

    public void setTituloPantalla(String tituloPantalla) {
        this.tituloPantalla = tituloPantalla;
    }

    public boolean isExportar() {
        return exportar;
    }

    public void setExportar(boolean exportar) {
        this.exportar = exportar;
    }
    
}
