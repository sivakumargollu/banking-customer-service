package com.abcbank.counter.service.models;

import com.abcbank.counter.service.enums.Previlages;
import com.abcbank.counter.service.enums.Role;

import java.util.List;
import java.util.Map;

public class OperatorDetails {

	String operatorId;
	Role   role;
	Map    previliages;

	public OperatorDetails(String operatorId, Role role, Map<String, List<Previlages>> accessPreviliages) {
		this.previliages = accessPreviliages;
		this.operatorId = operatorId;
		this.role = role;
	}

	public OperatorDetails() {

	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Map getAccessPreviliages() {
		return previliages;
	}

	public void setAccessPreviliages(Map accessPreviliages) {
		this.previliages = accessPreviliages;
	}
}
