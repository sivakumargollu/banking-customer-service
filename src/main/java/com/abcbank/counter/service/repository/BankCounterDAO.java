package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.DBAdapter;
import com.abcbank.counter.service.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository

public class BankCounterDAO {

	@Autowired
	DBAdapter dbAdapter;

	public void saveCustomer(CustomerDetails customerDetails){
		dbAdapter.saveCustomer(customerDetails);
	}

	public Token saveToken(Token token) {
		dbAdapter.saveToken(token);
		return token;
	}

}