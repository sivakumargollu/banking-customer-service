package com.abcbank.counter.service.enums;

public enum CounterStatus {

	SERVING_CUSTOMER(2), AVAILABLE(1), NOT_AVAILABLE(3), CLOSED(4);

	int priority;

	CounterStatus(int priority) {
		this.priority = priority;
	}

}
