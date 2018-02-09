package com.abcbank.counter.service.models;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.LinkedList;

@Entity
@Table(name = "TOKEN")
public class Token implements Comparable<Token>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long        Id;

	String      tokenId;

	@Column(name = "CUSTOMER_ID")
	Long        customerId;

	@Column(name = "PRIORITY")
	@Enumerated(EnumType.STRING)
	Priority    priority;

	@Column(name = "REQUESTED_SERVICE")
	@Enumerated
	BankService reqService;

	DateTime    serveTime;

	@Column(name="CREATED_TIME", nullable = true,
			columnDefinition="TIMESTAMP default CURRENT_TIMESTAMP")
	DateTime createdTime;

	@Column(name = "TOKEN_STATUS")
	@Enumerated(EnumType.STRING)
	TokenStatus status;

	LinkedList<BankService> actionItems;

	public TokenStatus getStatus() {
		return status;
	}

	public void setStatus(TokenStatus status) {
		this.status = status;
	}

	public LinkedList<BankService> getActionItems() {
		return actionItems;
	}

	public void setActionItems(LinkedList<BankService> actionItems) {
		this.actionItems = actionItems;
	}

	public Token() {

	}

	public Token(Long customerId, Priority priority, BankService reqService) {
		this.customerId = customerId;
		this.priority = priority;
		this.reqService = reqService;
		this.status = TokenStatus.NEW;
		initActionItems();
	}

	private LinkedList<BankService> initActionItems() {
		actionItems = new LinkedList<BankService>();
		if (reqService != null){
			if(reqService.isMultiCounter()) {
				BankService[] services = MultiCounterServices.getActionItemsByService(reqService);
				if(services != null) {
					for (BankService service : services) {
						actionItems.add(service);
					}
				}
			} else {
				actionItems.add(reqService);
			}
		}

		return actionItems;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public BankService getReqService() {
		return reqService;
	}

	public void setReqService(BankService requestedService) {
		this.reqService = requestedService;
	}

	public DateTime getServeTime() {
		return serveTime;
	}

	public void setServeTime(DateTime serveTime) {
		this.serveTime = serveTime;
	}

	public DateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(DateTime createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public int compareTo(Token o) {
		if(o.serveTime.isAfter(this.serveTime)){
			return -1;
		}
		else if(o.serveTime.isBefore(this.serveTime)){
			return 1;
		}
		else {
			return 0;
		}
	}
}
