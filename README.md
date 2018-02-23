# banking-customer-service

Serve the customer based priority-factor configured in bankconfig.props
 Servetime (An attribute in Token class ) the main factor which brings the PREMIUM customers to be serve first. It's value derived from above priority-ratioconfigured in props file

Priority Queues will be maintain at Individual counters (BankCounter), Sending a new token to counter will be decided based on minimum expecting time i.e counter which have minimum que and  providing requested service

CounterManager (BankCounterManager) will pick appropiriate counter and assign the token to it.

BankCounter and BankCounterManager are workers (Threads) will be started at the begining of the application

When an multi-couter service requested, A <b> LinkedList </b> of 'actionItems' will be maintained in the Token class these action items will be traversed and an appropriate counter will be alloted on best effort. 
 
##### Token Processing Flow

![flowdesign](https://user-images.githubusercontent.com/10070580/36557916-90be44fe-182f-11e8-87df-2abc0a3f24e7.png)


#API Design

```
http://localhost:8080/ABCBank/toekn-service//{branchId}/token/new - Method post
```
Payload
``` 
{
  "priority":"PREMIUM",
  "bankServices": ["ACC_OPEN" , "ACC_STMT"], //requested services(MulticounterService by customer)
  "customerId":12345,
  "newCustomer":true,
  "customer":{
    "id":123456,
    "name":"Name",
    "phNo":"99889988778"
  },
   "address":{
      "city":"Kaikalur",
      "zipCode":"34343434"
    }
}
``` 
Reponse 
``` 
{
"tokenId": "PREMIUM-2",
"customerId": 2,
"priority": "PREMIUM",
"reqService": ["ACC_OPEN" , "ACC_STMT"],
"serveTime": null,
"createdTime": 1519324378875,
"status": "NEW",
"actionItems": [
  "REGISTRATION",
  "DEPOSIT",
  "PASSBOOK",
  "ACC_STMT"
],
"comments": {},
"id": 2
}
``` 

Single counter  operation
``` 
{
  "priority":"REGURLAR",
  "bankServices": ["WITHDRAW"], //requested services
  "customerId":12345,
  "newCustomer":true,
  "customer":{
    "id":123456,
    "name":"Name",
    "phNo":"99889988778"
  },
   "address":{
      "city":"Kaikalur",
      "zipCode":"34343434"
    }
}
``` 

Reponse 

``` 
{
"tokenId": "PREMIUM-2",
"customerId": 2,
"priority": "PREMIUM",
"reqService": ["WITHDRWA"],
"serveTime": null,
"createdTime": 1519324378875,
"status": "NEW",
"actionItems": [
  "REGISTRATION",
  "DEPOSIT",
  "PASSBOOK",
  "ACC_STMT"
],
"comments": {},
"id": 2
}
``` 

API to updates token attributes such as adding commnets, makring cancelled/completed etc..
```
2. http://localhost:8080/ABCBank/toekn-service/{branchId}/token/update
```

API which Returns token processing information 
```
http://localhost:8080/ABCBank/toekn-service/{branchId}/token/status
```

Counter related operation such modifying services at counter and assign operator counter will be handled in counter-service

API to return counter status
  ```
  [http://localhost:8080/ABCBank/counter-service/{branchId}/counter/status],methods=[GET]
  ```
API to return update the counter information i.e changing service status, availability etc..
 
  ```
  [http://localhost:8080/ABCBank/counter-service/{branchId}/counter/update],methods=[POST]
  ```
API to assign an operator to counter 
 ```
  [http://localhost:8080/ABCBank/counter-service/{branchId}/counter/assign/operator],methods=[POST]
 ```  
 
 API to read all operators information at bank
 
 ```
   http://localhost:8080/ABCBank/counter-service/{branchId}/counter/operators/info],methods=[GET]
 ```  
  
##### Class Diagram

![bankcounterservice](https://user-images.githubusercontent.com/10070580/36556135-2b70caae-182a-11e8-9355-f640ac620b26.jpg)

#DB Design

   ![db](https://user-images.githubusercontent.com/10070580/36557812-4c8746c8-182f-11e8-83ed-3fc06278debd.png)
   ![db2](https://user-images.githubusercontent.com/10070580/36557813-4cfa556e-182f-11e8-99ca-f4167d0f2a1b.png)


