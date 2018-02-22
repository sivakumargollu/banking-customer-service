package com.abcbank.counter.service.services;

import com.abcbank.counter.service.entities.OperatorDetails;
import com.abcbank.counter.service.entities.OperatorXCounter;
import com.abcbank.counter.service.entities.Token;
import com.abcbank.counter.service.repository.CounterRepository;
import com.abcbank.counter.service.workers.BankCounter;
import com.abcbank.counter.service.workers.CounterManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
public class ApplicationBootStrapService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	CounterManager counterManager;

	@Autowired
	CounterRepository counterRepository;

	@Value("${bankcounter-resource}")
	String counterResourceFile;

	@Value("${operator-resource}")
	String operatorResourceFile;

	@Value("${operator-x-counter-resource}")
	String operatorXCounterResourceFile;

	@Value("${bankcounter.refresh-intraval}")
	Long counterRefreshIntraval;


	Logger logger = LoggerFactory.getLogger(ApplicationBootStrapService.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		ArrayList<BankCounter> bankCounterList = intializeCounters();
		counterManager.setBankCounters(bankCounterList);
		ArrayList<BankCounter> counters = counterManager.getBankCounters();
		//Since BankCounter intialization not done by spring container, setting values to component.
		for (BankCounter counter : counters) {
			counter.setCounterManager(counterManager);
			counter.setCounterRepository(counterRepository);
		}

		for (BankCounter counter : counters) {
			counterRepository.saveBankCounter(counter, false);
		}
		new Thread(counterManager).start();
		counterManager.startCounters();
		loadOperatorDetails();
		loadCounterOperatorMapping();
	}

	/**
	 * Reads the json file from resource and intialized the BankCounter information.
	 */
	public ArrayList<BankCounter> intializeCounters() {
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<BankCounter> bankCounters = new ArrayList<BankCounter>();
		final TypeReference<ArrayList<BankCounter>> type = new TypeReference<ArrayList<BankCounter>>() {
		};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream(counterResourceFile), type);
			counterManager.setBankCounters(bankCounters);
		} catch (IOException e) {
			logger.error("Error in intializing ", e);
		}

		//setting empty que
		for (BankCounter bankCounter : bankCounters) {
			bankCounter.setTokenQue(new PriorityQueue<Token>());
			bankCounter.setRefreshIntraval(counterRefreshIntraval);
		}
		counterManager.startCounters();
        return bankCounters;
	}

	/**
	 * Reads the json file from resource and intialized the Operators information.
	 */
	public ArrayList<OperatorDetails> loadOperatorDetails() {
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<OperatorDetails> operatorDetails = new ArrayList<OperatorDetails>();
		final TypeReference<ArrayList<OperatorDetails>> type = new TypeReference<ArrayList<OperatorDetails>>() {
		};
		try {
			operatorDetails = objectMapper.readValue(classLoader.getResourceAsStream(operatorResourceFile), type);
			if(operatorDetails.size() > 0){
				for (OperatorDetails operatorDetail : operatorDetails) {
					counterRepository.saveOpeatorDetails(operatorDetail, false);
				}
				counterManager.setOperatorDetailsList(operatorDetails);
			}
		} catch (IOException e) {
			logger.error("Error in intializing ", e);
		}
		return operatorDetails;
	}

	/**
	 * Reads the json file from resource and intialized the Operators information.
	 */
	public ArrayList<OperatorXCounter> loadCounterOperatorMapping() {
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<OperatorXCounter> operatorXCounters = new ArrayList<OperatorXCounter>();
		final TypeReference<ArrayList<OperatorXCounter>> type = new TypeReference<ArrayList<OperatorXCounter>>() {
		};
		try {
			operatorXCounters = objectMapper.readValue(classLoader.getResourceAsStream(operatorXCounterResourceFile), type);
			if(operatorXCounters.size() > 0){
				for (OperatorXCounter operatorXCounter : operatorXCounters) {
					counterRepository.saveOperatorXCounter(operatorXCounter, false);
				}
			}
			counterManager.setOperatorXCounters(operatorXCounters);
		} catch (IOException e) {
			logger.error("Error in intializing ", e);
		}
		return operatorXCounters;
	}


	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
