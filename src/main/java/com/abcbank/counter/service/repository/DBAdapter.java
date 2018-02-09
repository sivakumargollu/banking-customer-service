package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;

public interface DBAdapter<T> {

	T getConnection(boolean isNew);

	CustomerDetails saveCustomer(CustomerDetails customerDetails);

	Token saveToken(Token token);

}
