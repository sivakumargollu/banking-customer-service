package com.abcbank.counter.service.workers;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.CounterStatus;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import com.abcbank.counter.service.repository.BankCounterRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
public class BankCounterManager implements Runnable {

	PriorityQueue<BankCounter>   bankCounters;
	ConcurrentLinkedQueue<Token> waitingTokens;

	@Value("${bankcounter-resource}")
	String counterResourceFile;

	@Value("${priority-factor}")
	String priorityFactor;

	@Value("${bankcounter.manager.refresh-intraval}")
	Long refreshIntraval;

	@Value("${bankcounter.refresh-intraval}")
	Long counterRefreshIntraval;



	@Autowired
	BankCounterRepository bankCounterRepository;

	public ConcurrentLinkedQueue<Token> getWaitingTokens() {
		return waitingTokens;
	}

	public void setWaitingTokens(ConcurrentLinkedQueue<Token> waitingTokens) {
		this.waitingTokens = waitingTokens;
	}

	public int addWaitingToken(Token token) {
		waitingTokens.add(token);
		return waitingTokens.size();
	}

	Logger logger = LoggerFactory.getLogger(BankCounterManager.class);

	public PriorityQueue<BankCounter> getBankCounters() {
		if (bankCounters == null) {
			intializeCounters();
		}
		return bankCounters;
	}

	public void setBankCounters(PriorityQueue<BankCounter> bankCounters) {
		this.bankCounters = bankCounters;
	}

	public BankCounterManager(String counterResourceFile) {
		this.counterResourceFile = counterResourceFile;
		waitingTokens = new ConcurrentLinkedQueue<Token>();
		//intializeCounters();
	}

	public BankCounterManager() {
		waitingTokens = new ConcurrentLinkedQueue<Token>();
	}

	public void assignCounter(Token token) {
		if (bankCounters == null) {
			intializeCounters();
		}

		LinkedList<BankService> itemList  = new LinkedList<BankService>(token.getActionItems());
		BankService reqService = itemList.pollFirst();
		//Looping through all counters to find the one which can serve the current token
		for (BankCounter counter : bankCounters) {
			if(Arrays.asList(counter.getAvailableServices()).contains(reqService) ){
				//Got a counter
				PriorityQueue<Token> q = counter.getTokenQue();
				token.setReqService(reqService);
				long serveTime = q.size() * reqService.getAvgTimeRequiredInMin() * 60000; //into milli seconds
				if (token.getPriority().equals(Priority.PREMIUM)) {
					serveTime = serveTime / Integer.parseInt(priorityFactor.split(":")[1]);
				}
				token.setServeTime(new Date(DateTime.now().plus(serveTime).getMillis()));
				bankCounterRepository.updateToken(token.clone());
				q.add(token);
				counter.setTokenQue(q);
				break;
			}
		}
		token.setActionItems(itemList);
	}

	public void intializeCounters() {

		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		final TypeReference<PriorityQueue<BankCounter>> type = new TypeReference<PriorityQueue<BankCounter>>() {
		};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream(counterResourceFile), type);
		} catch (IOException e) {
			logger.error("Error in intializing ", e);
		}

		//setting empty que
		for (BankCounter bankCounter : bankCounters) {
			bankCounter.setTokenQue(new PriorityQueue<Token>());
			bankCounter.setRefreshIntraval(counterRefreshIntraval);
		}
		startCounters();

	}

	public void startCounters() {
		for (BankCounter bankCounter : bankCounters) {
			new Thread(bankCounter).start();
		}
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void run() {
		List<TokenStatus> allowedStatus = Arrays.asList(TokenStatus.NEW, TokenStatus.FORWARDED);
		while (true) {
			if (waitingTokens.isEmpty()) {
				try {
					Thread.sleep(refreshIntraval);
					refresh();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				while (waitingTokens.size() > 0) {
					Token token = waitingTokens.poll();
					if (allowedStatus.contains(token.getStatus())) {
						assignCounter(token);
					}
				}
			}
		}
	}

	public List<BankCounter> getCounterStatus() {
		ArrayList<BankCounter> counters = new ArrayList<>();
		if (getBankCounters() != null) {
			return new ArrayList<BankCounter>(getBankCounters());
		} else {
			return counters;
		}
	}
	public List<BankCounter> getCounterStatus(String counterID) {
		return getBankCounters().stream().filter(bankCounter -> bankCounter.getCounterId().equals(counterID)).collect(Collectors.toList());
	}

	//Lookup for new tokens
	public void refresh() {
		List<Token> tokenList = bankCounterRepository.readTokens(TokenStatus.NEW);
		waitingTokens.addAll(tokenList);
	}

	//updates counter status
	public BankCounter updateCounterStatus(String counterId, CounterStatus counterStatus) throws Exception {
		 List<BankCounter> bankCounters = getBankCounters().stream().filter(bankCounter -> bankCounter.getCounterId().equals(counterId)).collect(Collectors.toList());
		 if(bankCounters.size() > 0){
		 	BankCounter counter = bankCounters.get(0);
		 	counter.setStatus(counterStatus);
		 	return counter;
		 } else {
		 	throw new Exception("No counter available with given counterId");
		 }
	}
}
