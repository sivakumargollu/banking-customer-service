package com.abcbank.counter.service;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.*;
import com.abcbank.counter.service.repository.DBAdapter;
import com.abcbank.counter.service.services.BankCounterService;
import com.abcbank.counter.service.workers.BankCounter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Table;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankCounterServiceApplicationTests {

	@Autowired
	DBAdapter dbAdapter;

	@Autowired
	BankCounterService bankCounterService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldGetCorrectBankServiceWithName(){
		BankService bankService = BankService.get("DEPOSIT");
		Assert.assertEquals(bankService, BankService.DEPOSIT);
	}

	@Test
	public void shouldGetNullWithIncorrectName(){
		BankService bankService = BankService.get("DEPOST");
		Assert.assertEquals(bankService, null);
	}

	@Test
	public void shouldSaveToken() {
		Token token = new Token(1234l, Priority.PREMIUM, BankService.ACC_OPEN);
		token = dbAdapter.saveToken(token);
		Assert.assertTrue(token != null);
	}

	@Test
	public void mustSaveCustomer() {
		Customer customer = new Customer();
		customer.setCustomerId(1234l);
		customer.setPhNo("9999999");
		customer.setName("SivaKumar");

		Address address = new Address();
		address.setZipCode("1234567");
		address.setCity("KAIKALUR");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		customerDetails.setAddress(address);
		customerDetails.setPriority(Priority.PREMIUM);
		customerDetails.setBankService(BankService.ACC_OPEN);
		customerDetails.setNewCustomer(true);

		CustomerDetails details = dbAdapter.saveCustomer(customerDetails);
		Assert.assertTrue(details.getCustomer().getCustomerId() > 0);
		Assert.assertTrue(details.getAddress().getAddressId() > 0);
	}

	@Test
	public void shouldReturnValidToken() {
		Customer customer = new Customer();
		customer.setCustomerId(1234l);
		customer.setPhNo("9999999");
		customer.setName("SivaKumar");

		Address address = new Address();
		address.setZipCode("1234567");
		address.setCity("KAIKALUR");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		customerDetails.setAddress(address);
		customerDetails.setPriority(Priority.PREMIUM);
		customerDetails.setBankService(BankService.ACC_OPEN);
		customerDetails.setNewCustomer(true);

		Token token = bankCounterService.createToken(customerDetails);
		Assert.assertTrue(token != null);
		Assert.assertTrue(token.getId() > 0)	;
	}

	@Test
	public void shouldGetCounterStatus() {

		List<BankCounter> counterList = bankCounterService.counterStatus(null);
		Assert.assertTrue(counterList != null);
		Assert.assertTrue(counterList.size() == 4);

	}

    @Test
	public void shouldReturnSpecificCounterWithID() {

		List<BankCounter> counterList = bankCounterService.counterStatus("ABCBANK-B1-C3");
		Assert.assertTrue(counterList != null);
		Assert.assertTrue(counterList.size() == 1);

	}


	@Test
	public void shouldReturnTokenList() {

		Customer customer = new Customer();
		customer.setCustomerId(1234l);
		customer.setPhNo("9999999");
		customer.setName("SivaKumar");

		Address address = new Address();
		address.setZipCode("1234567");
		address.setCity("KAIKALUR");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		customerDetails.setAddress(address);
		customerDetails.setPriority(Priority.REGULAR);
		customerDetails.setBankService(BankService.ACC_OPEN);
		customerDetails.setNewCustomer(true);

		Token token = bankCounterService.createToken(customerDetails);
		Assert.assertTrue(token != null);
		Assert.assertTrue(token.getId() > 0)	;

		List<Token> tokenList = dbAdapter.readTokens(TokenStatus.NEW);
		Assert.assertTrue(tokenList != null);
		Assert.assertTrue(tokenList.size() > 0);
	}

	@Test
	public void getCounterStatus() {
		List<BankCounter> counterList = bankCounterService.counterStatus(null);
		Assert.assertTrue(counterList != null);
		Assert.assertTrue(counterList.size() > 0);

	}
}
