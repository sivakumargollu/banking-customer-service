package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class BankCounterRepository {

	@Autowired
	BankCounterDAO bankCounterDAO;

	public BankCounterRepository() {

	}

	public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
		bankCounterDAO.saveCustomer(customerDetails);
		return customerDetails;
	}

	public Token createToken(CustomerDetails customerDetails) {
		Long customerId = customerDetails.getCustomer().getCustomerId();
		Priority priority = customerDetails.getPriority();
		LinkedList<BankService> requestedServices = customerDetails.getBankServices();
		Token token = new Token(customerId, priority, requestedServices);
		bankCounterDAO.saveToken(token);
		token.setTokenId(token.getPriority().name() + "-" + token.getId());
		bankCounterDAO.updateToken(token);
		return token;
	}

	public Token updateToken(Token token) {
		bankCounterDAO.updateToken(token);
		return token;
	}

	public TokenXCounter updateTokenCounterStatus(TokenXCounter tokenXCounter){
		bankCounterDAO.updateTokenCounterStatus(tokenXCounter);
		return tokenXCounter;
	}

	public List<TokenXCounter> getTokenStatus(Long tokenId){
		return bankCounterDAO.getTokenStatus(tokenId);
	}

	public List<Token> readTokens(TokenStatus tokenStatus){
		return bankCounterDAO.readTokens(tokenStatus);
	}
}
