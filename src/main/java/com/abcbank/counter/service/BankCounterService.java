package com.abcbank.counter.service;

import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenStatus;
import com.abcbank.counter.service.repository.BankCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/ABCBank/counter-service/")
@RestController
public class BankCounterService {

	@Autowired
	BankCounterRepository bankCounterRepository;

	@RequestMapping(value = "/token/new", method = RequestMethod.POST)
	@ResponseBody
	public Token createToken(@RequestBody CustomerDetails customerDetails) {
		if(customerDetails.isNewCustomer()) {
			bankCounterRepository.saveCustomerDetails(customerDetails);
		}
		Token token = bankCounterRepository.createToken(customerDetails);
		bankCounterRepository.addTokenToQue(token);
		return token;
	}

	@RequestMapping(value = "/token/process/", method = RequestMethod.POST)
	public String processToken(@RequestBody OperatorDetails operatorDetails, @RequestParam("tokenId") String tokenId ) {

		return "Process";
	}

	@RequestMapping(value = "/token/status/update", method = RequestMethod.POST)
	public String updateToken(@RequestParam String tokenId, @RequestParam TokenStatus tokenStatus, OperatorDetails operatorDetails) {
        return "Updated";
	}



}
