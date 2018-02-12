# banking-customer-service

Serve the customer based priority factor configured in bankconfig.props
 
Servetime (attribute in Token class )is the main factor which brings the PREMIUM customers to be serve first. 

Priority QUEs maintain at Individual counters (BankCounter), New token will be alloted based on minimum expecting time i.e counter which have minimum que and  providing requested service

CounterManager (BankCounterManager ) will pick appropiriage counter and assign the token to it.

BankCounter and BankCounterManager are workers will started at the start of application
 


