package com.abcbank.counter.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;

@SpringBootApplication
public class BankCounterServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankCounterServiceApplication.class, args);
	}
}
