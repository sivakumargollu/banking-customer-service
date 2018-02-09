package com.abcbank.counter.service.services;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.repository.BankCounterRepository;
import com.abcbank.counter.service.workers.BankCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequestMapping("/ABCBank/counter-service")
@RestController
@Transactional
public class BankCounterService {

	@Autowired
	BankCounterRepository bankCounterRepository;

	@RequestMapping(value = "/token/new", method = RequestMethod.POST)
	@ResponseBody
	public Token createToken(@RequestBody CustomerDetails customerDetails) {
		if (customerDetails.isNewCustomer()) {
			bankCounterRepository.saveCustomerDetails(customerDetails);
		}
		Token token = bankCounterRepository.createToken(customerDetails);
		bankCounterRepository.addToken(token);
		return token;
	}

	@RequestMapping(value = "/counter/status", method = RequestMethod.GET)
	public List<BankCounter> updateToken() {
		return bankCounterRepository.getCounterStatus();
	}
}
