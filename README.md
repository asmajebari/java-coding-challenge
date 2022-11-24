# Crewmeister Test Assignment - Java Backend Developer

## The Challenge

The task was to create a foreign exchange rate service as SpringBoot-based microservice. 

The exchange rates were eceived from [2]. This is a public service provided by the German central bank.

As Crewmeister are using user story format, here are the user stories that were implemented:

- As a client, I want to get a list of all available currencies
- As a client, I want to get all EUR-FX exchange rates at all available dates as a collection
- As a client, I want to get the EUR-FX exchange rate at particular day
- As a client, I want to get a foreign exchange amount for a given currency converted to EUR on a particular day

For this service, I have used H2 which was recommended for simplicity.
 
## Setup
#### Requirements
- Java 11 (will run with OpenSDK 15 as well)
- Maven 3.x

#### Project
The project was generated through the Spring initializer [1] for Java
 11 with dev tools and Spring Web as dependencies. In order to build and 
 run it, you just need to click the green arrow in the Application class in your Intellij 
 CE IDE or run the following command from your project root und Linux or ios. 

````shell script
$ mvn spring-boot:run
````
## Solution
### IMPORTANT
For this challenge, I have opted for using an H2 database, which will store data received from the API. I have supposed that in a production environment, the database would already be setup, so the first thing to do is to go to http://localhost:8080/api/init-database?startDate=2022-01-01 , in your brower or your favorite API platform, as an example to initialize the database with data retrieved from the API starting from the past year. 

I recommend using the startDate query parameter and typing in a starting date to retrieve data instead of simply initializing the database with all the data received from the API (by going to http://localhost:8080/api/init-database) because it would initialize the database much faster, as storing all the data available from the API would take so much time, and this would still give you the ability to use the service.


I have opted for this solution because I believe that, for the client, the response time would be much faster than simply retrieving the data from the API and serving it back.

The result of initiliazing the database would be something like this:

![image](https://user-images.githubusercontent.com/61097141/203678043-d93f8e04-8d94-4081-9225-0c2cb42204bd.png)

You can now check the database, which will have two tables, by going to http://localhost:8080/h2-console on your browser.

### Using the service

Now that the database is all setup, we can test the service!

For all four user stories, I created four endpoints.

For the last request, a CurrencyConversionData object is required in body, this is an example: 
```
{
   "amount": "200",
   "date": "2022-09-06",
   "currency":"USD"
}
```

Request | Method | Endpoint | Query Parameters | Body
----------- | ----------- | ----------- | ----------- | -----------
Get all currencies | GET | http://localhost:8080/api/currencies/all | None | None
Get all exchange rates | GET | http://localhost:8080/api/currencies/exchange-rates | None | None
Get all exchange rates by date | GET | http://localhost:8080/api/currencies/exchange-rates/search| date | None
Get converted amount from given currency to EURO on given date | POST | http://localhost:8080/api/currencies/conversion | None | CurrencyConversionData object

Let's try out these endpoints!

#### Get all currencies available:
![image](https://user-images.githubusercontent.com/61097141/203680325-c4c38761-960d-423f-b0d4-cd088d9a2f3b.png)

#### Get all EUR-FX exchange rates at all available dates:
![image](https://user-images.githubusercontent.com/61097141/203680553-dd088a58-ee0a-4057-9da7-fb7480943a4f.png)

#### Get all EUR-FX exchange rates at a particular day:
![image](https://user-images.githubusercontent.com/61097141/203680711-198ef807-036a-4c78-9811-6439b1ec01f7.png)

#### Convert a foreign exchange amount for a given currency to EUR on a particular day:
![image](https://user-images.githubusercontent.com/61097141/203680920-85886fe5-6798-435a-a917-f5eecb0b724b.png)


*Side Note:*
You can see that retrieving data from the API on a particular day can take more than to 1s compared to 16ms by retrieving the data from the database.

![image](https://user-images.githubusercontent.com/61097141/203682684-a530d0b3-043b-4d52-bb1a-cd09f669c668.png)

I should also mention that I have setup a cron job on 4PM of every day, which retrieves the latest data from the service and saves it to the database, because the service updates its data at that time.

---

[1] https://start.spring.io/

[2] [Bundesbank Daily Exchange Rates](https://www.bundesbank.de/dynamic/action/en/statistics/time-currencies-databases/time-currencies-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE)
