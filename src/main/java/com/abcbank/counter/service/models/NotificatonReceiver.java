package com.abcbank.counter.service.models;

public interface NotificatonReceiver<T> {

	public void recieve(T obj);
}
