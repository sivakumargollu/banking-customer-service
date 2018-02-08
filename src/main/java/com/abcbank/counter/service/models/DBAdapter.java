package com.abcbank.counter.service.models;

import java.sql.Connection;

public interface DBAdapter {

	Connection getConnection();

	CustomerDetails saveCustomer(CustomerDetails customerDetails);
	Token saveToken(Token token);

}
