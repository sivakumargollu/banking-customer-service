package com.abcbank.counter.service;

import com.abcbank.counter.service.enums.*;
import com.abcbank.counter.service.entities.*;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.repository.DBAdapter;
import com.abcbank.counter.service.services.BankCounterService;
import com.abcbank.counter.service.workers.BankCounter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

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
		customerDetails.setBankServices(getBankServiceList(new BankService[] {BankService.ACC_OPEN}));
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
		customerDetails.setBankServices(getBankServiceList(new BankService[] {BankService.ACC_OPEN}));
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
		customerDetails.setBankServices(getBankServiceList(new BankService[] {BankService.ACC_OPEN}));
		customerDetails.setNewCustomer(true);

		Token token = bankCounterService.createToken(customerDetails);
		Assert.assertTrue(token != null);
		Assert.assertTrue(token.getId() > 0)	;

		List<Token> tokenList = dbAdapter.readTokens(TokenStatus.NEW);
		Assert.assertTrue(tokenList != null);
		Assert.assertTrue(tokenList.size() > 0);
	}

	@Test
	public void tokenShoulAllotToCounter() throws InterruptedException {
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
		customerDetails.setBankServices(getBankServiceList(new BankService[] {BankService.ACC_OPEN}));
		customerDetails.setNewCustomer(true);

		Token token = bankCounterService.createToken(customerDetails);
		Assert.assertTrue(token != null);
		Assert.assertTrue(token.getId() > 0)	;

		List<BankCounter> counterList = bankCounterService.counterStatus("ABCBANK-B1-C4");
		Assert.assertTrue(counterList != null);
		Assert.assertTrue(counterList.size() > 0);
		boolean counterAssigned = false;
		BankCounter counter = counterList.get(0);
		for (int i=0; i< 50; i++){
			Thread.sleep(1000);
			counterAssigned = counter.getTokenQue().size() > 0;
			if(counterAssigned){
				break;
			}
		}
		Assert.assertTrue(counterAssigned);
	}

	@Test
	public void shouldUpdateCounterStatus() throws Exception {
		BankCounter counter =  getTestBankCounter();
		counter.setStatus(CounterStatus.CLOSED);
		bankCounterService.update(counter);
		BankCounter counter1 = bankCounterService.counterStatus(counter.getCounterId()).get(0);
		Assert.assertEquals(counter1.getStatus(), CounterStatus.CLOSED);
	}

	@Test
	public void shouldUpdateCounterServiceStatus() throws Exception {
		BankCounter counter =  getTestBankCounter();
		HashMap<BankService, Boolean> serviceStatusMap = new HashMap<>();
		serviceStatusMap.put(BankService.ACC_STMT, false);
		counter.setServices(serviceStatusMap);
		bankCounterService.update(counter);
		BankCounter counter1 = bankCounterService.counterStatus(counter.getCounterId()).get(0);
		Assert.assertEquals(counter1.getServices().get(BankService.ACC_STMT), false);
	}

	@Test
	public void customMultiCounterServiceTest() {

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
		customerDetails.setBankServices(getBankServiceList(new BankService[] {BankService.WITHDRAW, BankService.ACC_OPEN}));
		customerDetails.setNewCustomer(true);

		Token token = bankCounterService.createToken(customerDetails);
		Assert.assertTrue(token != null);
		Assert.assertTrue(token.getId() > 0)	;
	}

	public LinkedList<BankService> getBankServiceList(BankService[] services) {
		LinkedList<BankService> bankServices = new LinkedList<>();
		for (BankService service : services) {
			bankServices.add(service);
		}
		return bankServices;
	}

	@Test
	public void testInitLoadCounters() {
		PriorityQueue<BankCounter> bankCounters = new PriorityQueue<>();
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		final TypeReference<PriorityQueue<BankCounter>> type = new TypeReference<PriorityQueue<BankCounter>>() {};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream("counters.json"), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
        Assert.assertTrue(bankCounters != null);
		Assert.assertEquals(bankCounters.size(), 4);
	}

	public BankCounter getTestBankCounter() {
		PriorityQueue<BankCounter> bankCounters = new PriorityQueue<>();
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		final TypeReference<PriorityQueue<BankCounter>> type = new TypeReference<PriorityQueue<BankCounter>>() {};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream("counters.json"), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bankCounters.peek();
	}
}
