package com.cp.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		String forward;	// Cadena de texto para navigation-rule en "faces-config.xml"
		
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletResponse res = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();

		if (name.length() > 0 && password.length() > 0) {
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