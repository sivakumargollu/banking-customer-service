package com.abcbank.counter.service.workers;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.CounterStatus;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.OperatorDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import com.abcbank.counter.service.repository.BankCounterDAO;
import com.abcbank.counter.service.repository.BankCounterRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.PriorityQueue;

@Component
public class BankCounter implements Comparable<BankCounter>, Runnable, Cloneable {

	BankService[]   availableServices;
	CounterStatus   status;
	OperatorDetails operatorDetails;
	String          counterId;
	Long refreshIntraval;

	@JsonIgnore
	@Autowired
	BankCounterManager bankCounterManager;

	@JsonIgnore
	@Autowired
	BankCounterRepository bankCounterRepository;

	public BankCounterManager getBankCounterManager() {
		return bankCounterManager;
	}

	public void setBankCounterManager(BankCounterManager bankCounterManager) {
		this.bankCounterManager = bankCounterManager;
	}

	public BankCounterRepository getBankCounterRepository() {
		return bankCounterRepository;
	}

	public void setBankCounterRepository(BankCounterRepository bankCounterRepository) {
		this.bankCounterRepository = bankCounterRepository;
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

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	PriorityQueue<Token> tokenQue;

	public PriorityQueue<Token> getTokenQue() {
		return tokenQue;
	}

	public void setTokenQue(PriorityQueue<Token> tokenQue) {
		this.tokenQue = tokenQue;
	}

	public BankCounter() {
	}

	public BankCounter(String counterId, BankService[] availableServices, CounterStatus status, OperatorDetails operatorDetails) {
		this.availableServices = availableServices;
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

	public BankService[] getAvailableServices() {
		return availableServices;
	}

	public void setAvailableServices(BankService[] availableServices) {
		this.availableServices = availableServices;
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
			logger.info("Serving token at " + operatorDetails.toString() + ", Service type " + token.getReqService().name());
			Thread.sleep(service.getAvgTimeRequiredInMin()); //serving token
			this.status = CounterStatus.AVAILABLE;
			if (token.getActionItems().size() > 0) {
				token.setStatus(TokenStatus.FORWARDED);
			} else {
				token.setStatus(TokenStatus.COMPLETED);
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
						TokenXCounter tokenXCounter = new TokenXCounter(token.getId(), counterId, token.getStatus());
						bankCounterRepository.updateTokenCounterStatus(tokenXCounter);
						bankCounterRepository.updateToken(token.clone());
						if(token.getStatus().equals(TokenStatus.FORWARDED)){
							bankCounterManager.addWaitingToken(token);
						}
					}

				}
			}
		} catch (Exception ex) {
			logger.error("Error while starting counter ", ex);
		}
	}
}
