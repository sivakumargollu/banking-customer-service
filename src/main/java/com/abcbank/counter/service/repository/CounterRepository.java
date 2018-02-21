package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.exceptions.DataNotFoundException;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class CounterRepository {

	@Autowired
	BankCounterDAO bankCounterDAO;

	public CounterRepository() {

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

	public Token updateToken(Long tokenId, TokenStatus tokenStatus,
			BankService service, String comment, Priority priority) throws DataNotFoundException {
		List<Token> tokens = readTokens(null); //Reading all tokens
		Token filteredToken = tokens.stream().filter(token -> token.getId().longValue() == tokenId).findAny().orElse(null);
		if (filteredToken == null) {
			throw new DataNotFoundException("Token not available with given token Id");
		}

		if (tokenStatus != null) {
			filteredToken.setStatus(tokenStatus);
		}
		if (service != null && comment != null && comment.length() > 0) {
			Map<BankService, String> existingComments = filteredToken.getComments();
			if(existingComments != null){
				String existingCommentOnSrvc = existingComments.get(service) != null ? existingComments.get(service) : "";
				existingComments.put(service, existingCommentOnSrvc + comment);
			}
		}

		if(priority != null){
			filteredToken.setPriority(priority);
		}
		return bankCounterDAO.updateToken(filteredToken.clone());
	}

	public TokenXCounter updateTokenCounterStatus(TokenXCounter tokenXCounter) {
		bankCounterDAO.updateTokenCounterStatus(tokenXCounter);
		return tokenXCounter;
	}

	public List<TokenXCounter> getTokenStatus(Long tokenId) {
		return bankCounterDAO.getTokenStatus(tokenId);
	}

	public List<Token> readTokens(TokenStatus tokenStatus) {
		return bankCounterDAO.readTokens(tokenStatus);
	}
}
