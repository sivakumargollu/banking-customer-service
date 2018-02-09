package com.abcbank.counter.service.models;

import java.sql.Connection;

public interface DBAdapter<T> {

	T getConnection(boolean isNew);

	CustomerDetails saveCustomer(CustomerDetails customerDetails);
	Token saveToken(Token token);

}
