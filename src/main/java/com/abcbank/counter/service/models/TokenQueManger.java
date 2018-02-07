package com.abcbank.counter.service.models;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

@Component
public class TokenQueManger implements QueManager<Token> {

	public static TreeMap<BankService, PriorityQueue<Token>> cachedQues;
	private String priorityRatio;

	public TokenQueManger(String priorityRatio) {
		this.priorityRatio = priorityRatio;
	}

	@Override
	public void refreshQue() {
		if(cachedQues == null || cachedQues.isEmpty()){
			intializeQues();
		}
		refresh();
	}

	@Override
	public void addToQue(Token token) {
		if(cachedQues.get(token.getRequestedService()) == null){
			initializeQue(token.requestedService, cachedQues);
		}
		PriorityQueue<Token> q = cachedQues.get(token.getRequestedService());
		setApproximateServeTime(token, q);
		q.add(token);

	}

	private void intializeQues() {
		cachedQues = new TreeMap<>();
		for (BankService serviceType : BankService.values()) {
			initializeQue(serviceType, cachedQues);
		}
	}

	 private void initializeQue(BankService service, TreeMap<BankService, PriorityQueue<Token>> cachedQues){
		if(cachedQues != null) {
			cachedQues.put(service, new PriorityQueue<Token>());
		}
	}

	 private void refresh() {
		List<Token> availableTokens  = new ArrayList<>(); // Fetch all waiting tokens
		for (Token token : availableTokens) {
			BankService serviceType = token.getRequestedService();
			PriorityQueue<Token> serviceTypeQue = cachedQues.get(serviceType);
			setApproximateServeTime(token, serviceTypeQue);
			serviceTypeQue.add(token);
			cachedQues.put(serviceType, serviceTypeQue);
		}
	}

	private Long getMilliseconds(long minutes){
		return minutes * 60000;
	}

	private void setApproximateServeTime(Token token, PriorityQueue<Token> q){
		int totalMinutesToServeWholeQue = q.size() * token.getRequestedService().avgTimeRequiredInMin;
		long totalTimeInMillSec = getMilliseconds(totalMinutesToServeWholeQue);
		if(token.getPriority().equals(Priority.PREMIUM)){
			DateTime serveTime = DateTime.now().plus(totalTimeInMillSec/Integer.parseInt(priorityRatio.split(":")[1]));
			token.setApproximateServingTime(serveTime);
		}
		token.setApproximateServingTime(DateTime.now().plus(totalTimeInMillSec));
	}
}
