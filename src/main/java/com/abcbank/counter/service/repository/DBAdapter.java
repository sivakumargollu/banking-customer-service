package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.entities.Token;
import com.abcbank.counter.service.entities.TokenXCounter;
import com.abcbank.counter.service.workers.BankCounter;

import java.util.List;

public interface DBAdapter<T> {

	T getConnection(boolean isNew);

	CustomerDetails saveCustomer(CustomerDetails customerDetails);

	Token saveToken(Token token);

	Token updateToken(Token token);

	TokenXCounter updateTokenCounterStatus(TokenXCounter tokenXCounter);

	List<TokenXCounter> getTokenStatus(Long tokenId);

	List<Token> readTokens(TokenStatus status);

	BankCounter saveBankCounter(BankCounter counter, boolean isUpdate);

}
