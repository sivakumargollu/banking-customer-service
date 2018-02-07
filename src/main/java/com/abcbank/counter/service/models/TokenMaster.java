package com.abcbank.counter.service.models;

import org.joda.time.DateTime;

import java.util.List;
import java.util.PriorityQueue;

public class TokenMaster implements NotificatonReceiver<BankCounter>, Runnable {

	TokenQueManger tokenQueManger;
	List<BankCounter> counters;
	BankTimings bankTimings;

	public TokenQueManger getTokenQueManger() {
		return tokenQueManger;
	}

	public void setTokenQueManger(TokenQueManger tokenQueManger) {
		this.tokenQueManger = tokenQueManger;
	}

	public List<BankCounter> getCounters() {
		return counters;
	}

	public void setCounters(List<BankCounter> counters) {
		this.counters = counters;
	}

	public void startServing() {

	}

	@Override
	public void recieve(BankCounter obj) {
		counters.add(obj);
	}

	@Override
	public void run() {
		DateTime now = DateTime.now();
		if(now.isAfter(bankTimings.getClosingTime().getMillis())){
			try {
				Thread.sleep(100l); //todo caliculate time upto next bank open session
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(now.isAfter(bankTimings.getStartTime().getMillis())
				&& now.isBefore(bankTimings.getClosingTime().getMillis())){
			PriorityQueue<Token> eligibleTokenToServe = new PriorityQueue<>();
		    if(counters.size() > 0){
		    	BankCounter counter = counters.get(0);
		    	if(counter.status.equals(CounterStatus.AVAILABLE)){
					for (BankService service : counter.services) {
						eligibleTokenToServe.add(TokenQueManger.cachedQues.get(service).peek());
					}
					if(eligibleTokenToServe.size() <= 0){
						//continue since no tokens available.
					} else {
						Token highEligibleTokenToServe = eligibleTokenToServe.poll();
						counter.setToken(highEligibleTokenToServe);
						counter.isNewToken(true);
					}
				}
			}
		}
	}
}
