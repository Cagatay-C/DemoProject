package com.cagatay.DemoProject.dao;

import java.util.List;

import com.cagatay.DemoProject.entities.User;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserDAO {
	private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	public boolean create(User user) {
		boolean result = true;
		if(checkUserEmailOrNameExist(user))
			return false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			session.save(user);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUserList() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<User> users = null;

		try {
			transaction = session.beginTransaction();
			users = session.createQuery("FROM User").list();
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return users;
	}
	
	public User getUserByName(String name) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		User user = null;

		try {
			transaction = session.beginTransaction();
			user = (User) session.createQuery("FROM User u WHERE u.name = :name")
					.setParameter("name", name).getSingleResult();
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return user;
	}
	
	public Boolean checkUserEmailOrNameExist(User user) {
		boolean result = false;
		List<User> users = getUserList();
		String name = user.getName();
		String email = user.getEmail();
		for(User u: users) {
			if(u.getName().equals(name) || u.getEmail().equals(email)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public Boolean login(String userName, String pass) {
		boolean result = false;
		User user = getUserByName(userName);
		if(user != null) {
			if(user.getPassword().equals(pass)) {
				result = true;
			}
		}
		return result;
	}
}
