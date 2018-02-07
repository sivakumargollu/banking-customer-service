package com.abcbank.counter.service.models;

import org.joda.time.DateTime;

public class Token implements Comparable<Token>{

	Long        Id;
	String      tokenId;
	Long        customerId;
	Priority    priority;
	BankService requestedService;
	DateTime    approximateServingTime;

	public Token() {

	}

	public Token(Long customerId, Priority priority, BankService requestedService) {
		this.customerId = customerId;
		this.priority = priority;
		this.requestedService = requestedService;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public BankService getRequestedService() {
		return requestedService;
	}

	public void setRequestedService(BankService requestedService) {
		this.requestedService = requestedService;
	}

	public DateTime getApproximateServingTime() {
		return approximateServingTime;
	}

	public void setApproximateServingTime(DateTime approximateServingTime) {
		this.approximateServingTime = approximateServingTime;
	}

	@Override
	public int compareTo(Token o) {
		if(o.approximateServingTime.isAfter(this.approximateServingTime)){
			return -1;
		}
		else if(o.approximateServingTime.isBefore(this.approximateServingTime)){
			return 1;
		}
		else {
			return 0;
		}
	}
}
