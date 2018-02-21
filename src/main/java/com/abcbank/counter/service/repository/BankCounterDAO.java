package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.entities.Token;
import com.abcbank.counter.service.entities.TokenXCounter;
import com.abcbank.counter.service.workers.BankCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BankCounterDAO {

	@Autowired
	DBAdapter dbAdapter;

	public void saveCustomer(CustomerDetails customerDetails) {
		dbAdapter.saveCustomer(customerDetails);
	}

	public Token saveToken(Token token) {
		dbAdapter.saveToken(token);
		return token;
	}

	public Token updateToken(Token token) {
		dbAdapter.updateToken(token);
		return token;
	}

	public TokenXCounter updateTokenCounterStatus(TokenXCounter tokenXCounter){
		dbAdapter.updateTokenCounterStatus(tokenXCounter);
		return tokenXCounter;
	}
	public List<TokenXCounter> getTokenStatus(Long tokenId){
		return dbAdapter.getTokenStatus(tokenId);
	}

	public List<Token> readTokens(TokenStatus tokenStatus) {
		return dbAdapter.readTokens(tokenStatus);
	}

	public BankCounter saveBankCounter(BankCounter counter, boolean isUpdate){
		return dbAdapter.saveBankCounter(counter, isUpdate);
	}

}
