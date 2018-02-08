package com.abcbank.counter.service.models;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;
import java.sql.*;

@Repository
public class H2DBAdapter implements DBAdapter {

	static Connection connection;

	@Override
	public Connection getConnection() {
		try {
			Class.forName("org.h2.Driver");
			if (connection == null) {
				connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=- 1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	@Override
	public CustomerDetails saveCustomer(CustomerDetails details) {
		try {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
			session.save(details.getCustomer());
			session.getTransaction().commit();
			session.beginTransaction();
			return details;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return details;
	}

	@Override
	public Token saveToken(Token token) {
		return token;
	}
}
