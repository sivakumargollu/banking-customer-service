package com.abcbank.counter.service.models;


public interface QueManager<T> {

	public void refreshQue();
	public void addToQue(T item);
}
