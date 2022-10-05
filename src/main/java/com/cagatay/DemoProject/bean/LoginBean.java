package com.cagatay.DemoProject.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.cagatay.DemoProject.dao.UserDAO;
import com.cagatay.DemoProject.entities.User;
import com.cagatay.DemoProject.util.Util;

import org.primefaces.PrimeFaces;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean {

	private String userName;
	private String password;
	private FacesMessage  message;

	private UserDAO userDAO;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String login() {
		userDAO = new UserDAO();
		message = null;
        boolean loggedIn = false;
		boolean result = userDAO.login(userName, password);
		String page = "login";
		if (result) {
			User currentUser = userDAO.getUserByName(userName);
			HttpSession session = Util.getSession();
			session.setAttribute("username", userName);
			session.setAttribute("userId", currentUser.getId());
			loggedIn = true;
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", userName);
			page = "index";
		}else {
			loggedIn = false;
			message = new FacesMessage(FacesMessage.SEVERITY_WARN,"Loggin Error", "Invalid Credentials");			
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);

		return page + "?facesRedirect=true";
	}

	public String logout() {
		HttpSession session = Util.getSession();
		session.invalidate();
		message = new FacesMessage(FacesMessage.SEVERITY_INFO, "You have been logged out!",userName);
		FacesContext.getCurrentInstance().addMessage(null, message);
		userName = null;
		password = null;
		return "login?facesRedirect=true";
	}

}
