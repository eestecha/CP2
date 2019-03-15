package com.cp._comun;

import java.io.Serializable;
/**
 * @author Emilio Estecha 2013
 *
 */
public class BeanParaTablaHash implements Serializable {
    public static final long serialVersionUID = 1L; // Para evitar "warning: [serial] serializable class..."

    private String key;
    private String value;
    
    public BeanParaTablaHash() {
        this.setKey(null);
        this.setValue(null);
    }
    public BeanParaTablaHash(String k, String v) {
        this.setKey(k);
        this.setValue(v);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
