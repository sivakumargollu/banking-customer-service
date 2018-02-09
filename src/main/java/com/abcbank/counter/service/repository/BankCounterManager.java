package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
public class BankCounterManager implements Runnable {

	PriorityQueue<BankCounter> bankCounters;
	LinkedList<Token> waitingTokens;

	@Value("${bankcounter-resource}")
	String counterResourceFile;

	@Value("${priority-factor}")
	String priorityFactor;

	public LinkedList<Token> getWaitingTokens() {
		return waitingTokens;
	}

	public void setWaitingTokens(LinkedList<Token> waitingTokens) {
		this.waitingTokens = waitingTokens;
	}

	public int addWaitingToken(Token token){
		waitingTokens.add(token);
		return waitingTokens.size();
	}

	Logger logger = LoggerFactory.getLogger(BankCounterManager.class);

	public PriorityQueue<BankCounter> getBankCounters() {
		if(bankCounters == null){
			intializeCounters();
		}
		return bankCounters;
	}

	public void setBankCounters(PriorityQueue<BankCounter> bankCounters) {
		this.bankCounters = bankCounters;
	}

	public BankCounterManager(String counterResourceFile){
		this.counterResourceFile = counterResourceFile;
		intializeCounters();
	}

	public BankCounterManager(){

	}

	public void assignCounter(Token token){
		if(bankCounters == null){
			intializeCounters();
		}
		BankCounter counter = bankCounters.poll();
		PriorityQueue<Token> q = counter.getTokenQue();
		BankService service = token.getActionItems().pollFirst();
		token.setReqService(service);
		long serveTime = q.size() * service.getAvgTimeRequiredInMin() * 60000; //into milli seconds
		if(token.getPriority().equals(Priority.PREMIUM)){
			serveTime = serveTime / Integer.parseInt(priorityFactor.split(":")[1]);
		}
		token.setServeTime(DateTime.now().plus(serveTime));
		counter.getTokenQue().add(token);
		bankCounters.add(counter);
	}

	public void intializeCounters() {

		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		final TypeReference<PriorityQueue<BankCounter>> type = new TypeReference<PriorityQueue<BankCounter>>() {};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream(counterResourceFile), type);
		} catch (IOException e) {
			logger.error("Error in intializing " ,  e);
		}

		//setting empty que
		for (BankCounter bankCounter : bankCounters) {
			bankCounter.setTokenQue(new PriorityQueue<Token>());
		}
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
		while (true){
			if(waitingTokens.isEmpty()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				while ( waitingTokens.size() > 0) {
					Token token = waitingTokens.pollFirst();
					if(Arrays.asList(TokenStatus.NEW, TokenStatus.FORWARDED).contains(token.getStatus())) {
						assignCounter(token);
					} else {
						//update DB
					}
				}
			}
		}
	}
}
