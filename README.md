# banking-customer-service

Serve the customer based priority-factor configured in bankconfig.props
 Servetime (An attribute in Token class ) the main factor which brings the PREMIUM customers to be serve first. It's value derived from above priority-ratioconfigured in props file

Priority Queues will be maintain at Individual counters (BankCounter), Sending a new token to counter will be decided based on minimum expecting time i.e counter which have minimum que and  providing requested service

CounterManager (BankCounterManager) will pick appropiriate counter and assign the token to it.

BankCounter and BankCounterManager are workers (Threads) will be started at the begining of the application

When an multi-couter service requested, A <b> LinkedList </b> of 'actionItems' will be maintained in the Token class these action items will be traversed and an appropriate counter will be alloted on best effort. 
 


