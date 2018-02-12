package com.abcbank.counter.service.enums;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public enum BankService {

	WITHDRAW(5, FALSE),
	DEPOSIT(5, FALSE),
	ACC_OPEN(10, TRUE),
	ACC_STMT(3, FALSE),
	ATM_SERVICES(5, FALSE),
	REGISTRATION(2, FALSE),
	PASSBOOK(2, FALSE);

	int     avgTimeRequiredInMin;
	boolean isMultiCounter;

	BankService(int avgTimeRequiredInMin, boolean isMultiCounter) {
		this.avgTimeRequiredInMin = avgTimeRequiredInMin;
		this.isMultiCounter = isMultiCounter;
	}

	public int getAvgTimeRequiredInMin() {
		return avgTimeRequiredInMin;
	}

	public void setAvgTimeRequiredInMin(int avgTimeRequiredInMin) {
		this.avgTimeRequiredInMin = avgTimeRequiredInMin;
	}

	public boolean isMultiCounter() {
		return isMultiCounter;
	}

	public void setMultiCounter(boolean multiCounter) {
		isMultiCounter = multiCounter;
	}

	public static BankService get(String name) {
		for (BankService service : BankService.values()) {
			if (service.name().equals(name)) {
				return service;
			}
		}
		return null;
	}
}

