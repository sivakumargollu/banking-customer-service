package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BankCounterRepository {

	@Autowired
	BankCounterDAO bankCounterDAO;

	@Autowired
	BankCounterManager bankCounterManager;

	public BankCounterRepository(){

	}
	public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
		bankCounterDAO.saveCustomer(customerDetails);
		return customerDetails;
	}

	public Token createToken(CustomerDetails customerDetails) {
		Long customerId = customerDetails.getCustomer().getCustomerId();
		Priority priority = customerDetails.getPriority();
		BankService requestedService = customerDetails.getBankService();
		Token token = new Token(customerId, priority, requestedService);
		bankCounterDAO.saveToken(token);
        return token;
	}

	public void addToken(Token token) {
		bankCounterManager.addWaitingToken(token);
	}

	public List<BankCounter> getCounterStatus() {
		ArrayList<BankCounter> counters = new ArrayList<>();
		if(bankCounterManager != null) {
			return new ArrayList<BankCounter>(bankCounterManager.getBankCounters());
		} else {
			return counters;
		}
	}

}
