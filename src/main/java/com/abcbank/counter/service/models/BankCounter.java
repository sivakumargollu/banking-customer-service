package com.abcbank.counter.service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BankCounter implements Comparable<BankCounter> , Runnable {

	BankService[] services;
	CounterStatus status;
	OperatorDetails operatorDetails;
	String counterId;

	public String getCounterId() {
		return counterId;
	}

	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}

	@JsonIgnore
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	PriorityQueue<Token> priorityQueue;

	public PriorityQueue<Token> getPriorityQueue() {
		return priorityQueue;
	}

	public void setPriorityQueue(PriorityQueue<Token> priorityQueue) {
		this.priorityQueue = priorityQueue;
	}

	public BankCounter(){}

	public BankCounter(String counterId, BankService[] services, CounterStatus status, OperatorDetails operatorDetails) {
		this.services = services;
		this.status = status;
		this.operatorDetails = operatorDetails;
		this.counterId = counterId;
	}

	public OperatorDetails getOperatorDetails() {
		return operatorDetails;
	}

	public void setOperatorDetails(OperatorDetails operatorDetails) {
		this.operatorDetails = operatorDetails;
	}

	public BankService[] getServices() {
		return services;
	}

	public void setServices(BankService[] services) {
		this.services = services;
	}

	Logger logger = LoggerFactory.getLogger(BankCounter.class);

	public CounterStatus getStatus() {
		return status;
	}

	public void setStatus(CounterStatus status) {
		this.status = status;
	}

	public void serve(Token token)  {
		try {
			logger.info("Serving token at " + operatorDetails.toString() + ", Service type " + token.getRequestedService().name());
			Thread.sleep(token.getRequestedService().avgTimeRequiredInMin); //serving token
			this.status = CounterStatus.AVAILABLE;
		} catch (InterruptedException ie) {
			logger.error(ie.getStackTrace().toString());
		}

	}

	@Override
	public int compareTo(BankCounter o) {
		if(o != null && o.priorityQueue != null && this.priorityQueue != null) {

			if (o.priorityQueue.size() > this.priorityQueue.size()) {
				return -1;
			} else if (o.priorityQueue.size() < this.priorityQueue.size()) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void run() {
		try {
			while (priorityQueue.isEmpty()) {
				Thread.sleep(5000);
			}
		} catch (Exception ex){
			logger.error("Error while starting counter ", ex);
		}
		for (Token token : priorityQueue) {
			serve(token);
		}
	}
}
