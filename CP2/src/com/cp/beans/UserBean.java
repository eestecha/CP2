package com.cp.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.FuentesDeDatos.BDConexion;
import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;

@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "";
	private String password = "";

	public String login() {

		String forward; // Cadena de texto para navigation-rule en
						// "faces-config.xml"

		if (name.length() > 0 && password.length() > 0) {

			//////////////////////////////////////////////////////////////////////

			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			
			BDConexion dataBase = new Subrutinas().getBDConexion(request);
			
			com.cp.mo_modo.db.MoAccesoBaseDatos dao = new com.cp.mo_modo.db.MoAccesoBaseDatos();
			com.cp.mo_modo.bean.MoBean			reg = new com.cp.mo_modo.bean.MoBean();
			
			// PK:
			reg.setMo_id_modo( Subrutinas.getDateAuditoria() );
			
			reg.setMo_mo_nombre( this.getName() );
			reg.setMo_colectivo( this.getPassword() );
			
			try {
				dao.mo_crtObj(dataBase, reg);
			} catch (StExcepcion e) {
				e.printStackTrace();
			}

			//////////////////////////////////////////////////////////////////////

			forward = "welcome";
		} else {
			forward = "login";
		}

		return forward;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}