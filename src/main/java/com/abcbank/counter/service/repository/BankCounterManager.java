package com.abcbank.counter.service.repository;

import com.abcbank.counter.service.models.BankCounter;
import com.abcbank.counter.service.models.BankService;
import com.abcbank.counter.service.models.Priority;
import com.abcbank.counter.service.models.Token;
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
import java.util.PriorityQueue;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
public class BankCounterManager {

	PriorityQueue<BankCounter> bankCounters;

	@Value("${bankcounter-resource}")
	String counterResourceFile;

	@Value("${priority-factor}")
	String priorityFactor;

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

	public void addToken(Token token){

		if(bankCounters == null){
			intializeCounters();
		}
		BankCounter counter = bankCounters.poll();
		PriorityQueue<Token> q = counter.getPriorityQueue();
		BankService srvc = token.getRequestedService();
		long serveTime = q.size() * srvc.getAvgTimeRequiredInMin() * 60000; //into milli seconds
		if(token.getPriority().equals(Priority.PREMIUM)){
			serveTime = serveTime / Integer.parseInt(priorityFactor.split(":")[1]);
		}
		token.setApproximateServingTime(DateTime.now().plus(serveTime));
		counter.getPriorityQueue().add(token);
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
			bankCounter.setPriorityQueue(new PriorityQueue<Token>());
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
}
