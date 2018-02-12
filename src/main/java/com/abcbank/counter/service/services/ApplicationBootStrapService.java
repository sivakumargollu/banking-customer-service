package com.abcbank.counter.service.services;

import com.abcbank.counter.service.repository.BankCounterRepository;
import com.abcbank.counter.service.workers.BankCounter;
import com.abcbank.counter.service.workers.BankCounterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.PriorityQueue;

@Component
public class ApplicationBootStrapService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	BankCounterManager bankCounterManager;

	@Autowired
	BankCounterRepository bankCounterRepository;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		bankCounterManager.intializeCounters();
		PriorityQueue<BankCounter> counters = bankCounterManager.getBankCounters();
		//Since BankCounter intialization not done by spring container, setting values to component.
		for (BankCounter counter : counters) {
			counter.setBankCounterManager(bankCounterManager);
			counter.setBankCounterRepository(bankCounterRepository);
		}
		new Thread(bankCounterManager).start();
		bankCounterManager.startCounters();
	}
}
