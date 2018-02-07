package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankCounterRepository {

	@Autowired
	BankCounterDAO bankCounterDAO;

	@Autowired
	TokenQueManger tokenQueManger;

	public BankCounterRepository(){

	}
	public Integer saveCustomerDetails(CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();
		Address address = customer.getAddres();
		bankCounterDAO.getEntityManager().persist(customer);
		customer.setAddres(address);
		return 0;
	}

	public Token createToken(CustomerDetails customerDetails) {
		Long customerId = customerDetails.getCustomer().getId();
		Priority priority = customerDetails.getPriority();
		BankService requestedService = customerDetails.getBankService();
		Token token = new Token(customerId, priority, requestedService);
		bankCounterDAO.getEntityManager().persist(token);
        return token;
	}

	public void addTokenToQue(Token token) {
		tokenQueManger.addToQue(token);
	}

}
