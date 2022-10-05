package com.cagatay.DemoProject.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.cagatay.DemoProject.dao.UserDAO;
import com.cagatay.DemoProject.entities.User;

@RequestScoped
@ManagedBean(name = "userManagedBean")
public class UserManagedBean {

	private UserDAO userDAO;

	private User user;

	public UserManagedBean() {
		userDAO = new UserDAO();
		this.user = new User();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String save() {
		String page = "login";
		FacesMessage message = null;
		System.out.println("before dao");
		boolean result = userDAO.create(this.user);
		if (result) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration succesful", this.user.getName());
		}else {
			page = "register";
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Registration Error", "Invalid Credentials");			
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return page + "?facesRedirect=true";
	}

	public void login() {
		System.out.println("Logged in");
	}
}
