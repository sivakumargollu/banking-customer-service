package com.abcbank.counter.service.models;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.TokenStatus;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.time.DateTime;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TokenXCounter")
public class TokenXCounter {

	public TokenXCounter(Long tokenId, String counterId, TokenStatus tokenStatus, BankService service) {
		this.tokenId = tokenId;
		this.counterId = counterId;
		this.tokenStatus = tokenStatus;
		this.service = service;
	}

	public TokenXCounter(){

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long Id;

	@Column(name = "TokenId")
	Long tokenId;

	@Column(name = "CounterId")
	String counterId;

	@Enumerated(EnumType.STRING)
	@Column(name = "TokenStatus")
	TokenStatus tokenStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "service")
	BankService service;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date updatedTime;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getCounterId() {
		return counterId;
	}

	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}

	public TokenStatus getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(TokenStatus tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public BankService getService() {
		return service;
	}

	public void setService(BankService service) {
		this.service = service;
	}
}
