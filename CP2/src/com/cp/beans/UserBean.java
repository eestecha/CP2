package com.cp.beans;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cp.FuentesDeDatos.BDConexion;
import com.cp._comun.StBean;
import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;

@ManagedBean(name = "user")
@SessionScoped
public class UserBean extends StBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "";
	private String password = "";
	private long favNumber1 = 0;

	
	
	/////////////////////////////////////////////
	public void doSomething() throws IOException {
	    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	    ec.getFlash().put("msg", "Something was done successfully");
	    ec.redirect("view.xhtml#msg");
	}
	/////////////////////////////////////////////
	

	public String pedito() {

		String forward = "Return de 'pedito(): '" + Subrutinas.getDateAuditoria();
		
		forward = "<div style='background-color: red;'>"
				+ "alert('Hola pajarete');"
				+ "</div>";

		System.out.println( getClass().getSimpleName() + ".pedito(): " + forward );

		return forward;
	}

	public String login() {

		String forward; // Cadena de texto para navigation-rule en
						// "faces-config.xml"

		if (name.length() > 0 && password.length() > 0) {

			//////////////////////////////////////////////////////////////////////
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

			request.getSession().setAttribute("logon_USR", this.getName());
			
			BDConexion dataBase = new Subrutinas().getBDConexion(request);
			
			com.cp.mo_modo.db.MoAccesoBaseDatos dao = new com.cp.mo_modo.db.MoAccesoBaseDatos();
			com.cp.mo_modo.bean.MoBean			reg = new com.cp.mo_modo.bean.MoBean();
			
			// PK:
			reg.setMo_id_modo( Subrutinas.getDateAuditoria() );
			
			reg.setMo_mo_nombre( this.getName() );
			reg.setMo_colectivo( this.getPassword() );
			
			this.setFavNumber1( -1L );
			try { this.setFavNumber1( Long.parseLong( reg.getMo_id_modo() ) ); } catch (NumberFormatException e1) {;} 
			
			try {
				dao.mo_crtObj(dataBase, reg);
			} catch (StExcepcion e) {
				e.printStackTrace();
			}

			//////////////////////////////////////////////////////////////////////

			forward = "OK";
		} else {
			forward = "login";
		}

		System.out.println( getClass().getSimpleName() + ".login(): " + this.toString() );

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

	public long getFavNumber1() {
		return favNumber1;
	}

	public void setFavNumber1(long favNumber1) {
		this.favNumber1 = favNumber1;
	}
}