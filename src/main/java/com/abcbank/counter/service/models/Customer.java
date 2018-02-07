package com.abcbank.counter.service.models;

import com.abcbank.counter.service.models.Address;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "NAME")
	String  name;

	String  phNo;

	Address address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhNo() {
		return phNo;
	}

	public void setPhNo(String phNo) {
		this.phNo = phNo;
	}

	public Address getAddres() {
		return address;
	}

	public void setAddres(Address addres) {
		this.address = addres;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
