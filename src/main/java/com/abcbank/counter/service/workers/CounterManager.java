package com.abcbank.counter.service.workers;

import com.abcbank.counter.service.enums.BankService;
import com.abcbank.counter.service.enums.CounterStatus;
import com.abcbank.counter.service.enums.Priority;
import com.abcbank.counter.service.enums.TokenStatus;
import com.abcbank.counter.service.models.Token;
import com.abcbank.counter.service.repository.CounterRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Component
@Configuration
@PropertySource("classpath:bankconfig.properties")
public class CounterManager implements Runnable {

	ArrayList<BankCounter>   bankCounters;
	ConcurrentLinkedQueue<Token> waitingTokens;

	@Value("${bankcounter-resource}")
	String counterResourceFile;

	@Value("${priority-factor}")
	String priorityFactor;

	@Value("${bankcounter.manager.refresh-intraval}")
	Long refreshIntraval;

	@Value("${bankcounter.refresh-intraval}")
	Long counterRefreshIntraval;



	@Autowired
	CounterRepository counterRepository;

	public ConcurrentLinkedQueue<Token> getWaitingTokens() {
		return waitingTokens;
	}

	public void setWaitingTokens(ConcurrentLinkedQueue<Token> waitingTokens) {
		this.waitingTokens = waitingTokens;
	}

	public int addWaitingToken(Token token) {
		waitingTokens.add(token);
		return waitingTokens.size();
	}

	Logger logger = LoggerFactory.getLogger(CounterManager.class);

	public ArrayList<BankCounter> getBankCounters() {
		return bankCounters;
	}

	public void setBankCounters(ArrayList<BankCounter> bankCounters) {
		this.bankCounters = bankCounters;
	}

	public CounterManager(String counterResourceFile) {
		this.counterResourceFile = counterResourceFile;
		waitingTokens = new ConcurrentLinkedQueue<Token>();
	}

	public CounterManager() {
		waitingTokens = new ConcurrentLinkedQueue<Token>();
	}

	/**
	 * 1. Picks an actionItem(service) from the Token
	 * 2. Will try to lookup a Counter which can serve the actionItem
	 * 3. If no counter is serving requested action item mark the Token as cancelled
	 * 4. Caliculates the approximate serve time based on que size at the Counter and set it Token
	 * 5. Then token will joined in que,
	 * 6. Respectivly relative updates happen
	 * @param token
	 */
	public void assignCounter(Token token) {
		LinkedList<BankService> itemList  = new LinkedList<BankService>(token.getActionItems());
		BankService reqService = itemList.pollFirst();
		BankCounter counter = findCounter(reqService);
		if(counter == null){
			logger.info("No counter serving " + token.getReqService().name() + " at the moment, marking token as CANCELLED");
			HashMap<BankService, String> comments = new HashMap<BankService, String>();
			comments.put(reqService,"No counter serving " + token.getReqService().name() + " at the moment, marking token as CANCELLED");
			token.setComments(comments);
			token.setStatus(TokenStatus.CANCELLED);
			counterRepository.updateToken(token.clone());
			return;
		}
		PriorityQueue<Token> tokenQue = counter.getTokenQue();
		token.setReqService(reqService);
		long serveTime = tokenQue.size() * reqService.getAvgTimeRequiredInMin() * 60000; //into milli seconds
		if (token.getPriority().equals(Priority.PREMIUM)) {
			serveTime = serveTime / Integer.parseInt(priorityFactor.split(":")[1]);
		}
		token.setServeTime(new Date(DateTime.now().plus(serveTime).getMillis()));
		counterRepository.updateToken(token.clone());
		logger.info("Adding token " + token.getId() + " to counter " + counter.getCounterId());
		tokenQue.add(token);
		counter.setTokenQue(tokenQue);
		token.setStatus(TokenStatus.ASSIGNED);
		counterRepository.updateToken(token.clone());
		token.setActionItems(itemList);
	}

	/**
	 * Reads the json file from resource and intialized the BankCounter information.
	 */
	public void intializeCounters() {
		ClassLoader classLoader = getClass().getClassLoader();
		ObjectMapper objectMapper = new ObjectMapper();
		final TypeReference<ArrayList<BankCounter>> type = new TypeReference<ArrayList<BankCounter>>() {
		};
		try {
			bankCounters = objectMapper.readValue(classLoader.getResourceAsStream(counterResourceFile), type);
		} catch (IOException e) {
			logger.error("Error in intializing ", e);
		}

		//setting empty que
		for (BankCounter bankCounter : bankCounters) {
			bankCounter.setTokenQue(new PriorityQueue<Token>());
			bankCounter.setRefreshIntraval(counterRefreshIntraval);
		}
		startCounters();

	}

	/**
	 * Make all counters as Runnable instances
	 */
	public void startCounters() {
		for (BankCounter bankCounter : bankCounters) {
			new Thread(bankCounter).start();
		}
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void run() {
		List<TokenStatus> allowedStatus = Arrays.asList(TokenStatus.NEW, TokenStatus.FORWARDED, TokenStatus.PICKED);
		while (true) {
			if (waitingTokens.isEmpty()) {
				try {
					Thread.sleep(refreshIntraval);
					refresh();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				while (waitingTokens.size() > 0) {
					Token token = waitingTokens.poll();
					if (allowedStatus.contains(token.getStatus())) {
						assignCounter(token);
					}
				}
			}
		}
	}

	/**
	 * Returns Counter Information i.e status, services, tokenQue at that given point of time.
	 * @return List<BankCounter>
	 */
	public List<BankCounter> getCounterStatus() {
		ArrayList<BankCounter> counters = new ArrayList<>();
		if (getBankCounters() != null) {
			return new ArrayList<BankCounter>(getBankCounters());
		} else {
			return counters;
		}
	}
	public List<BankCounter> getCounterStatus(String counterID) {
		return getBankCounters().stream().filter(bankCounter -> bankCounter.getCounterId().equals(counterID)).collect(Collectors.toList());
	}

	/**
	 * Reads the all tokens with 'NEW' status from database and change their status to PICKED
	 */
	public void refresh() {
		List<Token> tokenList = counterRepository.readTokens(TokenStatus.NEW);
		for (Token token : tokenList) {
			token.setStatus(TokenStatus.PICKED);
			counterRepository.updateToken(token.clone());
			waitingTokens.add(token);
		}
	}

	/**
	 *
	 * @param inputBankCounter
	 * @return
	 * @throws Exception
	 *
	 * STATUS, SERVICES, OPERATOR DETAILS INFORMATION CAN MODIFIED
	 * ALSO REFRESHES A COUNTER QUE IF IN CASE SERVICE CHANGE HAPPEN AT THE COUNTER.
	 */
	public BankCounter updateCounterStatus(BankCounter inputBankCounter) throws Exception {
		 List<BankCounter> bankCounters = getBankCounters()
						 .stream().filter(
						 		bankCounter -> bankCounter.getCounterId().equals(inputBankCounter.getCounterId())
						  ).collect(Collectors.toList());
		 if(bankCounters.size() > 0){
		 	BankCounter counter = bankCounters.get(0);
		 	if(!counter.getStatus().equals(inputBankCounter.getStatus())) {
				counter.setStatus(inputBankCounter.getStatus());
			}

			 for (BankService bankService : inputBankCounter.getServices().keySet()) {
				 counter.getServices().put(bankService, inputBankCounter.getServices().get(bankService));
			 }
			 notifyServiceChanges(counter);
		 	counter.setOperatorDetails(inputBankCounter.getOperatorDetails());
		 	return counter;
		 } else {
		 	throw new Exception("No counter available with given counterId");
		 }
	}

	/**
	 *
	 * @param service
	 * @return BankCounter
	 *
	 * Lookup for BankCounter which have minimum que,
	 * Counter with CounterStatus.CLOSING_SOON will not be considered
	 */
	BankCounter findCounter(BankService service) {

		PriorityQueue<BankCounter> counterPriorityQueue = new PriorityQueue<>();
		for (BankCounter counter : bankCounters) {
			if (counter.getServices().get(service) != null && counter.getServices().get(service)) {
				if(!counter.getStatus().equals(CounterStatus.CLOSING_SOON)) {
					counterPriorityQueue.add(counter);
				}
			}
		}
		return counterPriorityQueue.poll();
	}

	/**
	 *
	 * @param bankCounter
	 * @return
	 *
	 * It moves tokens with specific service to the waitingQue,
	 * if In case that requested service stopped/unavailable from the counter.
	 */
	private BankCounter notifyServiceChanges(BankCounter bankCounter){
		for (BankService bankService : bankCounter.getServices().keySet()) {
			boolean isServiceAvailable = bankCounter.getServices().get(bankService);
			if(!isServiceAvailable){
				PriorityQueue<Token> tokenQue = new PriorityQueue<>(bankCounter.getTokenQue());
				for (Token token : tokenQue) {
					if(token.getReqService().equals(bankService)){
						tokenQue.remove(token);
						waitingTokens.add(token);
					}
				}
				bankCounter.setTokenQue(tokenQue);
			}
		}
		return bankCounter;
	}
}
