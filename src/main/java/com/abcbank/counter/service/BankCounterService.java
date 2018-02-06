package com.abcbank.counter.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/ABCBank/counter-service/")
@RestController
public class BankCounterService {

	@RequestMapping("/token/new")
	public String createToken() {
		return "Token created";
	}
}
