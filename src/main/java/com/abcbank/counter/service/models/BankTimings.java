package com.abcbank.counter.service.models;

import org.joda.time.DateTime;

class BankTimings {

	DateTime startTime;
	DateTime closingTime;

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(DateTime closingTime) {
		this.closingTime = closingTime;
	}
}
