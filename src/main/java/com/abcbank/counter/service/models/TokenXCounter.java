package com.abcbank.counter.service.models;

import com.abcbank.counter.service.enums.TokenStatus;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "TokenXCounter")
public class TokenXCounter {

	public TokenXCounter(Long tokenId, String counterId, TokenStatus tokenStatus) {
		this.tokenId = tokenId;
		this.counterId = counterId;
		this.tokenStatus = tokenStatus;
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

	@Column(name = "updateTime", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
	DateTime updatedTime;

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

	public DateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(DateTime updatedTime) {
		this.updatedTime = updatedTime;
	}
}
