package com.abcbank.counter.service.exceptions;


public class ServiceException extends ABCBankException {

	public ServiceException(){

	}
	public ServiceException(String message){
		super(message);
	}

	public ServiceException(String message, Throwable throwable){
		super(message);
	}
}
