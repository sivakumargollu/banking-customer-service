package com.abcbank.counter.service.services;

import com.abcbank.counter.service.repository.CounterRepository;
import com.abcbank.counter.service.workers.BankCounter;
import com.abcbank.counter.service.workers.CounterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ApplicationBootStrapService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	CounterManager counterManager;

	@Autowired
	CounterRepository counterRepository;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		counterManager.intializeCounters();
		ArrayList<BankCounter> counters = counterManager.getBankCounters();
		//Since BankCounter intialization not done by spring container, setting values to component.
		for (BankCounter counter : counters) {
			counter.setCounterManager(counterManager);
			counter.setCounterRepository(counterRepository);
		}
		new Thread(counterManager).start();
		counterManager.startCounters();
	}
}
