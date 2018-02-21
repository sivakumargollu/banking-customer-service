package com.abcbank.counter.service.workers;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.CounterStatus;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.OperatorDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import com.abcbank.counter.service.repository.CounterRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.PriorityQueue;

@Component
public class BankCounter implements Comparable<BankCounter>, Runnable {

	HashSet<BankService> services;
	CounterStatus        status;
	OperatorDetails      operatorDetails;
	String               counterId;
	Long                 refreshIntraval;

	@JsonIgnore
	@Autowired
	CounterManager counterManager;

	@JsonIgnore
	@Autowired
	CounterRepository counterRepository;

	public CounterManager getCounterManager() {
		return counterManager;
	}

	public void setCounterManager(CounterManager counterManager) {
		this.counterManager = counterManager;
	}

	public CounterRepository getCounterRepository() {
		return counterRepository;
	}

	public void setCounterRepository(CounterRepository counterRepository) {
		this.counterRepository = counterRepository;
	}

	public String getCounterId() {
		return counterId;
	}

	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}

	public Long getRefreshIntraval() {
		return refreshIntraval;
	}

	public void setRefreshIntraval(Long refreshIntraval) {
		this.refreshIntraval = refreshIntraval;
	}

	PriorityQueue<Token> tokenQue;

	public PriorityQueue<Token> getTokenQue() {
		return tokenQue;
	}

	public void setTokenQue(PriorityQueue<Token> tokenQue) {
		this.tokenQue = tokenQue;
	}

	public BankCounter() {
	}

	public BankCounter(String counterId,
			HashSet<BankService> services,
			CounterStatus status, OperatorDetails operatorDetails) {
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

	public HashSet<BankService> getServices() {
		return services;
	}

	public void setServices(HashSet<BankService> services) {
		this.services = services;
	}

	Logger logger = LoggerFactory.getLogger(BankCounter.class);

	public CounterStatus getStatus() {
		return status;
	}

	public void setStatus(CounterStatus status) {
		this.status = status;
	}

	public Token serve(Token token) {
		try {
			BankService service = token.getReqService();
			logger.info("Serving token "+ token.getId() +" at " + counterId + ", Service type " + token.getReqService().name());
			Thread.sleep(service.getAvgTimeRequiredInMin() * 60000); //serving token
			this.status = CounterStatus.AVAILABLE;
			if (token.getActionItems().size() > 0) {
				token.setStatus(TokenStatus.FORWARDED);
				token.getComments().put(service, "Requested service " + service.name() + " done at " + counterId + "marking it as " + TokenStatus.FORWARDED.name());
			} else {
				token.setStatus(TokenStatus.COMPLETED);
				token.getComments().put(service, "Requested service " + service.name() + " done at " + counterId + "marking it as " + TokenStatus.COMPLETED.name());
			}
		} catch (InterruptedException ie) {
			logger.error(ie.getStackTrace().toString());
		}
         return token;
	}

	@Override
	public int compareTo(BankCounter o) {
		if (o != null && o.tokenQue != null && this.tokenQue != null) {
			if (o.tokenQue.size() > this.tokenQue.size()) {
				return -1;
			} else if (o.tokenQue.size() < this.tokenQue.size()) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (tokenQue.isEmpty()) {
					Thread.sleep(refreshIntraval);
				} else {
					while (!tokenQue.isEmpty()){
						Token token = serve(tokenQue.poll());
						TokenXCounter tokenXCounter = new TokenXCounter(token.getId(), counterId, token.getStatus(), token.getReqService());
						counterRepository.updateTokenCounterStatus(tokenXCounter);
						counterRepository.updateToken(token.clone());
						if(token.getStatus().equals(TokenStatus.FORWARDED)){
							counterManager.addWaitingToken(token);
						}
					}

				}
			}
		} catch (Exception ex) {
			logger.error("Error while starting counter ", ex);
		}
	}
}
