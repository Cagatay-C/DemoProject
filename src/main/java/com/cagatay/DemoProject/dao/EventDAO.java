package com.cagatay.DemoProject.dao;

import java.util.List;

import com.cagatay.DemoProject.entities.Event;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class EventDAO {
	private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	public boolean create(Event event) {
		boolean result = true;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			session.save(event);
			transaction.commit();
			result = true;
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

	public boolean update(Event event) {
		boolean result = true;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			session.update(event);
			transaction.commit();
			result = true;
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

	public boolean delete(Event event) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		boolean result = false;
		try {
			transaction = session.beginTransaction();
			session.delete(event);
			transaction.commit();
			result = true;
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			result = false;
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Event> getUserEvents(Long userId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Event> events = null;

		try {
			transaction = session.beginTransaction();
			events = (List<Event>) session.createQuery("FROM Event e WHERE e.user.id = :userId")
					.setParameter("userId", userId).list();

			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return events;
	}
}
