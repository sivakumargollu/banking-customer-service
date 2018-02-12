package com.abcbank.counter.service.models;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;

public class CustomerDetails {

	Customer    customer;
	Address     address;
	Priority    priority;
	BankService bankService;
	boolean     isNewCustomer;
	int         customerId;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	public boolean isNewCustomer() {
		return isNewCustomer;
	}

	public void setNewCustomer(boolean newCustomer) {
		isNewCustomer = newCustomer;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
