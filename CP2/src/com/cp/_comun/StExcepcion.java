package com.cp._comun;
/**
 * @author Emilio Estecha 2013
 *
 */
public class StExcepcion extends java.lang.Exception {
    public static final long serialVersionUID = 1L; // Para evitar "warning: [serial] serializable class...""
    public StExcepcion(String msg) {
        super(msg);
    }
}
