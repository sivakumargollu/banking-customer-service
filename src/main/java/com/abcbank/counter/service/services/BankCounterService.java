package com.abcbank.counter.service.services;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.exceptions.DataNotFoundException;
import com.abcbank.counter.service.exceptions.ServiceException;
import com.abcbank.counter.service.models.CustomerDetails;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.models.TokenXCounter;
import com.abcbank.counter.service.repository.CounterRepository;
import com.abcbank.counter.service.workers.BankCounter;
import com.abcbank.counter.service.workers.CounterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequestMapping("/ABCBank/counter-service")
@RestController
@Transactional
public class BankCounterService {

	@Autowired
	CounterRepository counterRepository;

	@Autowired
	CounterManager counterManager;

	@RequestMapping(value = "/token/new", method = RequestMethod.POST)
	@ResponseBody
	public Token createToken(@RequestBody CustomerDetails customerDetails) {
		if (customerDetails.isNewCustomer()) {
			counterRepository.saveCustomerDetails(customerDetails);
		}
		return counterRepository.createToken(customerDetails);
	}

	@RequestMapping(value = "/token/update", method = RequestMethod.POST)
	@ResponseBody
	public Token updateToken(@RequestParam Long tokenId, @RequestParam(required = false) TokenStatus tokenStatus,
			@RequestParam(required = false) BankService service, @RequestParam(required = false) String comments,
			@RequestParam(required = false) Priority priority) throws ServiceException {
		try {
			return counterRepository.updateToken(tokenId, tokenStatus, service, comments, priority);
		} catch (DataNotFoundException e){
			throw new ServiceException("Failed to serve request due to " + e.getMessage());
		}
	}

	@RequestMapping(value = "/counter/status", method = RequestMethod.GET)
	public List<BankCounter> counterStatus(@RequestParam(required = false) String counterId) {
		if(counterId != null && counterId.trim().length() > 0){
			return counterManager.getCounterStatus(counterId);
		} else  {
			return counterManager.getCounterStatus();
		}
	}

	@RequestMapping(value = "/token/status", method = RequestMethod.GET)
	public List<TokenXCounter> tokenStatus(@RequestParam(required = true) Long tokenId) throws Exception {
		if(tokenId == null){
			throw new Exception("Not a Valid token");
		}
		return counterRepository.getTokenStatus(tokenId);
	}

	@RequestMapping(value = "/counter/update", method = RequestMethod.POST)
	public BankCounter update(@RequestBody  BankCounter counter) throws Exception {
		return counterManager.updateCounterStatus(counter);
	}
}
