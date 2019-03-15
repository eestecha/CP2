
package com.cp._comun;

/**
 * @author Emilio Estecha 2013
 *
 */
public class RstAplicar {
    
    public String rwUpperCase = null;
    public String rwLike = null;
    public String rwAnyString = null;
    public final String COMILLAS = ""; // SI ES POSTGRESQL poner ===>  "\"";
    
    public RstAplicar( String rwUpperCase, String rwLike, String rwAnyString ) {
		this.rwUpperCase = rwUpperCase;
		this.rwLike = rwLike;
		this.rwAnyString = rwAnyString;
    }
    
    public String getCHAR_EQ_Estricto(String rst, String campo, String sqlWhere){
		if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		sqlWhere += COMILLAS + campo + COMILLAS;
		if (rst == null){
		    sqlWhere += " IS null";
		} else if ( rst != null && rst.trim().length()== 0 ) {
		    sqlWhere += " = ''";
		} else if ( rst != null && rst.trim().length() > 0 ) {
		    sqlWhere += " = '"  + rst.trim() + "'";
		}
		return sqlWhere;
    }
    
    public String getCHAR_NE_Estricto(String rst, String campo, String sqlWhere){
		if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		sqlWhere += COMILLAS + campo + COMILLAS;
		if (rst == null){
		    sqlWhere += " IS NOT null";
		} else if ( rst != null && rst.trim().length()== 0 ) {
		    sqlWhere += " <> ''";
		} else if ( rst != null && rst.trim().length() > 0 ) {
		    sqlWhere += " <> '"  + rst.trim() + "'";
		}
		return sqlWhere;
    }
    
    public String getCHAR_LIKE(String rst, String campo, String sqlWhere){
		//Para evitar distinción de cadenas por mayúsculas y minúsculas,
		//se transforman las cadenas a mayúsculas con la funcion de SQL 'UPPER'.
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND " + rwUpperCase +"("; else sqlWhere = " WHERE " + rwUpperCase +"(";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += ") " + rwLike + " '" + rwAnyString  + rst.trim().toUpperCase() + rwAnyString + "'";
		}
		return sqlWhere;
    }
    
    public String getCHAR_EQ(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " = '"  + rst.trim() + "'";
		}
		return sqlWhere;
    }
    
    public String getCHAR_NE(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " <> '"  + rst.trim() + "'";
		}
		return sqlWhere;
    }
    
    public String getNUM_EQ_Estricto(String rst, String campo, String sqlWhere){
		if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		sqlWhere += COMILLAS + campo + COMILLAS;
		if (rst == null){
		    sqlWhere += " IS null";
		} else if ( rst != null && rst.trim().length()== 0 ) {
		    sqlWhere += " = 0";
		} else if ( rst != null && rst.trim().length() > 0 ) {
		    sqlWhere += " = "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
    public String getNUM_NE_Estricto(String rst, String campo, String sqlWhere){
		if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		sqlWhere += COMILLAS + campo + COMILLAS;
		if (rst == null){
		    sqlWhere += " IS NOT null";
		} else if ( rst != null && rst.trim().length()== 0 ) {
		    sqlWhere += " <> 0";
		} else if ( rst != null && rst.trim().length() > 0 ) {
		    sqlWhere += " <> "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
    public String getNUM_EQ(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " = "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
    public String getNUM_NE(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " <> "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
    public String getNUM_GE(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " >= "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
    public String getNUM_GT(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " > "  + rst.trim() + "";
		}
		return sqlWhere;
    }

    public String getNUM_LT(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " < "  + rst.trim() + "";
		}
		return sqlWhere;
    }

    public String getNUM_LE(String rst, String campo, String sqlWhere){
		if ( rst != null && rst.trim().length() > 0 ) {
		    if (sqlWhere.trim().length()>0) sqlWhere += " AND "; else sqlWhere = " WHERE ";
		    sqlWhere += COMILLAS + campo + COMILLAS;
		    sqlWhere += " <= "  + rst.trim() + "";
		}
		return sqlWhere;
    }
    
}
