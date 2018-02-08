package com.abcbank.counter.service.models;

public enum BankService {

	WITHDRAW(5),
	DEPOSIT(5),
	ACC_OPEN(10),
	ACC_STMT(3),
	ATM_SERVICES(5);

	int avgTimeRequiredInMin;
	BankService(int avgTimeRequiredInMin){
		this.avgTimeRequiredInMin = avgTimeRequiredInMin;
	}

	public int getAvgTimeRequiredInMin() {
		return avgTimeRequiredInMin;
	}

	public void setAvgTimeRequiredInMin(int avgTimeRequiredInMin) {
		this.avgTimeRequiredInMin = avgTimeRequiredInMin;
	}
}

