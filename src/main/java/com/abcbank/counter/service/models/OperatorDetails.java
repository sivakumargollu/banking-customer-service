package com.abcbank.counter.service.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorDetails {
	String id;
	Role   role;
	Map    accessPreviliages;

	public OperatorDetails(String id, Role role,Map<String, List<Previlages>> accessPreviliages) {
		this.accessPreviliages = accessPreviliages;
		this.id = id;
		this.role = role;
	}
	public OperatorDetails(){

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Map getAccessPreviliages() {
		return accessPreviliages;
	}

	public void setAccessPreviliages(Map accessPreviliages) {
		this.accessPreviliages = accessPreviliages;
	}
}
