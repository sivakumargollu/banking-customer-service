package com.abcbank.counter.service.models;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;
import java.sql.*;

@Repository
public class H2DBAdapter implements DBAdapter {

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public CustomerDetails saveCustomer(CustomerDetails details) {
		try {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
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
