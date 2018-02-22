package com.abcbank.counter.service.services;

import com.abcbank.counter.service.entities.Token;
import com.abcbank.counter.service.entities.TokenXCounter;
import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.exceptions.DataNotFoundException;
import com.abcbank.counter.service.exceptions.ServiceException;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.repository.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequestMapping("/ABCBank/token-service")
@RestController
@Transactional
public class TokenService {

	@Autowired
	CounterRepository counterRepository;

	@RequestMapping(value = "/{branchId}/token/new", method = RequestMethod.POST)
	@ResponseBody
	public Token createToken(@PathVariable("branchId") String branchId, @RequestBody CustomerDetails customerDetails) {
		if (customerDetails.isNewCustomer()) {
			counterRepository.saveCustomerDetails(customerDetails);
		}
		return counterRepository.createToken(customerDetails, branchId);
	}

	@RequestMapping(value = "/{branchId}/token/update", method = RequestMethod.POST)
	@ResponseBody
	public Token updateToken(
			@PathVariable("branchId") String branchId,
			@RequestParam Long tokenId, @RequestParam(required = false) TokenStatus tokenStatus,
			@RequestParam(required = false) BankService service, @RequestParam(required = false) String comments,
			@RequestParam(required = false) Priority priority) throws ServiceException {
		try {
			return counterRepository.updateToken(tokenId, tokenStatus, service, comments, priority);
		} catch (DataNotFoundException e){
			throw new ServiceException("Failed to serve request due to " + e.getMessage());
		}
	}

	@RequestMapping(value = "/{branchId}/token/status", method = RequestMethod.GET)
	@ResponseBody
	public List<TokenXCounter> tokenStatus(
			@PathVariable("branchId") String branchId,
			@RequestParam(required = true) Long tokenId) throws Exception {
		if(tokenId == null){
			throw new Exception("Not a Valid token");
		}
		return counterRepository.getTokenStatus(tokenId);
	}
}
