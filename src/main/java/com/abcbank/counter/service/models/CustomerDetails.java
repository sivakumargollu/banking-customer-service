package com.abcbank.counter.service.models;

public class CustomerDetails {

	Customer Customer;
	Priority                                    priority;
	BankService                                 bankService;
	boolean                                     isNewCustomer;
	int                                         customerId;

	public Customer getCustomer() {
		return Customer;
	}

	public void setCustomer(Customer customer) {
		Customer = customer;
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
}
