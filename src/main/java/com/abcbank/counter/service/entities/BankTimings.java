package com.abcbank.counter.service.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
class BankTimings {

	//Time 24 hours format
	@Value("${bank-open-hour}")
	Integer startTime;

	@Value("${bank-closing-hour}")
	Integer closingTime;

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(Integer closingTime) {
		this.closingTime = closingTime;
	}
}
