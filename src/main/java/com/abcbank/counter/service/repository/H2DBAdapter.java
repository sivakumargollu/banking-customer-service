package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.entities.OperatorDetails;
import com.abcbank.counter.service.entities.OperatorXCounter;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.entities.Token;
import com.abcbank.counter.service.entities.TokenXCounter;
import com.abcbank.counter.service.workers.BankCounter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
	public Token updateToken(Token token) {
		Session session = getConnection(true);
		session.beginTransaction();
		session.update(token);
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

	@Override
	public List<Token> readTokens(TokenStatus status) {
		Session session = getConnection(true);
		session.beginTransaction();
		String hql = "FROM Token ";
		Query query = session.createQuery(hql);
		List<Token> retTokens = new ArrayList<>();
		if(status != null) {
			for (Token token : (List<Token>) query.list()) {
				if (token != null && token.getStatus().equals(status)) {
					retTokens.add(token);
				}
			}
		}
		//session.getTransaction().commit();
		//session.close();
		return retTokens;
	}

	@Override
	public BankCounter saveBankCounter(BankCounter counter, boolean isUpdate) {
		Session session = getConnection(true);
		session.beginTransaction();
		if(isUpdate){
			session.update(counter);
		} else {
			session.save(counter);
		}
		session.getTransaction().commit();
		session.close();
		return counter;
	}

	@Override
	public OperatorDetails saveOpeatorDetails(OperatorDetails operatorDetails, boolean isUpdate) {
		Session session = getConnection(true);
		session.beginTransaction();
		if(isUpdate){
			session.update(operatorDetails);
		} else {
			session.save(operatorDetails);
		}
		session.getTransaction().commit();
		session.close();
		return operatorDetails;
	}

	@Override
	public OperatorXCounter saveOperatorXCounter(OperatorXCounter operatorXCounter, boolean isUpdate) {
		Session session = getConnection(true);
		session.beginTransaction();
		if(isUpdate){
			session.update(operatorXCounter);
		} else {
			session.save(operatorXCounter);
		}
		session.getTransaction().commit();
		session.close();
		return operatorXCounter;
	}
}
