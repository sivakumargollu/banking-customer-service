package com.abcbank.counter.service;

import com.abcbank.counter.service.models.*;
import com.abcbank.counter.service.repository.BankCounterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		if(customerDetails.isNewCustomer()) {
			bankCounterRepository.saveCustomerDetails(customerDetails);
		}
		Token token = bankCounterRepository.createToken(customerDetails);
		bankCounterRepository.addTokenToQue(token);
		return token;
	}

	@RequestMapping(value = "/counter/status", method = RequestMethod.GET)
	public List<BankCounter> updateToken() {
        return bankCounterRepository.getCounterStatus();
	}

	public static void main(String[] args) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewCustomer(true);
		customerDetails.setCustomerId(123456);
		customerDetails.setBankService(BankService.ACC_OPEN);
		customerDetails.setPriority(Priority.REGULAR);
		Customer customer = new Customer();

		customer.setCustomerId(123456l);
		customer.setName("Siva Kumar");
		customer.setPhNo("99889988778");
		Address address = new Address();
		address.setCity("Kaikalur");
		address.setZipCode("34343434");
		customer.setAddress(address);
		customerDetails.setCustomer(customer);

		String json = objectMapper.writeValueAsString(customerDetails);
	}


}
