package com.abcbank.counter.service.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankCounter implements Comparable<BankCounter> , Runnable {

	BankService[] services;
	Token token;
	CounterStatus status;
	TokenMaster tokenMaster;
	Long counterId;
	boolean isNewToken;

	public boolean isNewToken() {
		return isNewToken;
	}

	public void setNewToken(boolean newToken) {
		isNewToken = newToken;
	}

	Logger logger = LoggerFactory.getLogger(BankCounter.class);

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	BankCounter(TokenMaster tokenMaster, BankService[] services) {
		this.tokenMaster = tokenMaster;
	}

	public CounterStatus getStatus() {
		return status;
	}

	public void setStatus(CounterStatus status) {
		this.status = status;
	}

	BankCounter(BankService[] services) {
		this.services = services;
	}

	public void serve()  {
		try {
			logger.info("Serving token at " + counterId + ", Service type " + token.getRequestedService().name());
			Thread.sleep(1000); //serving token
			this.status = CounterStatus.AVAILABLE;
			tokenMaster.recieve(this);
		} catch (InterruptedException ie) {
			logger.error(ie.getStackTrace().toString());
		}

	}

	@Override
	public int compareTo(BankCounter o) {
		if(o.status.priority > this.status.priority) {
			return -1;
		} else if(o.status.priority < this.status.priority) {
			return 1;
		}
		return 0;
	}

	@Override
	public void run() {
		while (!isNewToken) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			serve();
		}
	}
}
