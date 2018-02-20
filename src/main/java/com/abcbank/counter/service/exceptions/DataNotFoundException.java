package com.abcbank.counter.service.exceptions;

public class DataNotFoundException extends ABCBankException {

	public DataNotFoundException(){

	}
	public DataNotFoundException(String message){
		super(message);
	}
}
