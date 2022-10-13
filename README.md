# Account transfer with FX

Application has initially following opened accounts:


OWNER_ID  	BALANCE  	CURRENCY  
1111	    1000.0	         EUR
2222	    10000.0	         RON
3333	    2000.0	         USD
4444	    1500.0	         GBP

Endpoint available at following address:

http://localhost:8080/account-transfer

It is a post request with json body.

eg:
{
"sourceAccountId": 11112,
"destinationAccountId": 2222,
"amount": 100
}
