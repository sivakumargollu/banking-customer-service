package com.abcbank.counter.service.enums;

public enum MultiCounterServices {
	ACC_OPEN(BankService.ACC_OPEN, new BankService[] {
			BankService.get(BankService.REGISTRATION.name()),
			BankService.get(BankService.DEPOSIT.name()),
			BankService.get(BankService.PASSBOOK.name()) });

	BankService   service;
	BankService[] actionItems;

	MultiCounterServices(BankService service, BankService[] actionItems) {
		this.service = service;
		this.actionItems = actionItems;
	}

	public static BankService[] getActionItemsByService(BankService service) {
		for (MultiCounterServices multiCounterServices : MultiCounterServices.values()) {
			if (multiCounterServices.service.equals(service)) {
				return multiCounterServices.actionItems;
			}
		}
		return null;
	}
}
