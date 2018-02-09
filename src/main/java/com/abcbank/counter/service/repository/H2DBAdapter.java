package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public class H2DBAdapter implements DBAdapter<Session> {

	static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

	@Override
	public Session getConnection(boolean isNew) {
		if (isNew) {
			return sessionFactory.openSession();
		} else {
			return sessionFactory.getCurrentSession();
		}
	}

	@Override
	public CustomerDetails saveCustomer(CustomerDetails details) {
		try {
			Session session = getConnection(true);
			session.beginTransaction();
			session.save(details.getCustomer());
			details.getAddress().setCustomerId(details.getCustomer().getCustomerId());
			session.save(details.getAddress());
			session.getTransaction().commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return details;
	}

	@Override
	public Token saveToken(Token token) {
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(token);
		session.getTransaction().commit();
		session.close();
		return token;
	}
}
