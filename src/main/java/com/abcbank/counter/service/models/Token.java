package com.abcbank.counter.service.models;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.MultiCounterServices;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "TOKEN")
public class Token implements Comparable<Token> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long Id;

	String tokenId;

	@Column(name = "CUSTOMER_ID")
	Long customerId;

	@Column(name = "PRIORITY")
	@Enumerated(EnumType.STRING)
	Priority priority;

	@Column(name = "REQUESTED_SERVICE")
	@Enumerated
	BankService reqService;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	Date serveTime;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	Date createdTime;

	@Column(name = "TOKEN_STATUS")
	@Enumerated(EnumType.STRING)
	TokenStatus status;

	@Column
	@ElementCollection
	List<BankService> actionItems;

	public TokenStatus getStatus() {
		return status;
	}

	public void setStatus(TokenStatus status) {
		this.status = status;
	}

	public List<BankService> getActionItems() {
		return  actionItems;
	}

	public void setActionItems(LinkedList<BankService> actionItems) {
		this.actionItems = actionItems;
	}

	public Token() {

	}

	public 	Token(Long customerId, Priority priority, BankService reqService) {
		this.customerId = customerId;
		this.priority = priority;
		this.reqService = reqService;
		this.status = TokenStatus.NEW;
		initActionItems();
	}

	private LinkedList<BankService> initActionItems() {
		actionItems = new LinkedList<BankService>();
		if (reqService != null) {
			if (reqService.isMultiCounter()) {
				BankService[] services = MultiCounterServices.getActionItemsByService(reqService);
				if (services != null) {
					for (BankService service : services) {
						actionItems.add(service);
					}
				}
			} else {
				actionItems.add(reqService);
			}
		}

		return (LinkedList<BankService>) actionItems;
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

	public Date getServeTime() {
		return serveTime;
	}

	public void setServeTime(Date serveTime) {
		this.serveTime = serveTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public int compareTo(Token o) {
		DateTime thatServeTime = new DateTime(o.getServeTime());
		if (thatServeTime.isAfter(new DateTime(this.serveTime))) {
			return -1;
		} else if (thatServeTime.isBefore(new DateTime(this.serveTime))) {
			return 1;
		} else {
			return 0;
		}
	}
}
