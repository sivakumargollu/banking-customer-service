package com.abcbank.counter.service.services;

import com.abcbank.counter.service.entities.OperatorXCounter;
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

	@RequestMapping(value = "/{branchId}/counter/status", method = RequestMethod.GET)
	public List<BankCounter> counterStatus(
			@PathVariable("branchId") String branchId,
			@RequestParam(required = false) String counterId) {
		if(counterId != null && counterId.trim().length() > 0){
			return counterManager.getCounterStatus(counterId);
		} else  {
			return counterManager.getCounterStatus();
		}
	}

	/**
	 * Updates the token attibutes such it's availability status, services
	 * BranchId and refresh intraval.
	 * @param counter
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{branchId}/counter/update", method = RequestMethod.POST)
	public BankCounter updateBankCounter(@PathVariable("branchId") String branchId, @RequestBody  BankCounter counter) throws Exception {
		return counterManager.updateCounterStatus(counter);
	}

	/**
	 * Allocation of counter to operator, if the mapping exists it update the status
	 * @param operatorXCounterMapping
	 * @return
	 */
	@RequestMapping(value = "/{branchId}/counter/assign/operator", method = RequestMethod.POST)
	public OperatorXCounter saveOperatorXCounterInfo(@PathVariable("branchId") String branchId, OperatorXCounter operatorXCounterMapping){
		if(operatorXCounterMapping.getId() != null && operatorXCounterMapping.getId() > 0){
			return counterRepository.saveOperatorXCounter(operatorXCounterMapping, true);
		}else {
			return counterRepository.saveOperatorXCounter(operatorXCounterMapping, false);
		}
	}

	/**
	 * Return List of operators activly working at different counters in the branch, If counterId and operatorId passed they will act as filters
	 * @param branchId
	 * @param counterId
	 * @param operatorId
	 */
	@RequestMapping(value = "/{branchId}/counter/operators/info", method = RequestMethod.GET)
	public List<OperatorXCounter> getOperatorXCounterInfo(@PathVariable("branchId") String branchId,
			@RequestParam(required = false) String counterId,
			@RequestParam(required = false) String operatorId){
		return null;
	}
}
