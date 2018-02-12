package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;

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
		Session session = getConnection(true);
		session.beginTransaction();
		session.save(token);
		session.getTransaction().commit();
		session.close();
		return token;
	}

	@Override
	public TokenXCounter updateTokenCounterStatus(TokenXCounter tokenXCounter) {
		Session session = getConnection(true);
		session.beginTransaction();
		session.save(tokenXCounter);
		session.getTransaction().commit();
		session.close();
		return tokenXCounter;
	}

	@Override
	public List<TokenXCounter> getTokenStatus(Long tokenId) {
		Session session = getConnection(true);
		session.beginTransaction();
		String hql = "FROM TokenXCounter where tokenId =:tokenId";
		Query query = session.createQuery(hql);
		query.setParameter("tokenId", tokenId);
		return query.list();
	}
}
