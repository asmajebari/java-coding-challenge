package com.crewmeister.cmcodingchallenge.currency;

import com.crewmeister.cmcodingchallenge.currency.dto.*;
import com.crewmeister.cmcodingchallenge.currency.dto.Date;
import com.crewmeister.cmcodingchallenge.currency.dto.ResponseData;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRate;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRateId;
import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyConversionData;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyExchangeRateRepo;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    @Autowired
    private WebClient webClient;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    CurrencyExchangeRateRepo currencyExchangeRepo;

    // Use webClient to fetch data from API
    public WebClient.ResponseSpec fetchData(String URI){
       return webClient.get().uri(URI).retrieve();
    }

    // Fetch all data from API or chose data starting from certain date
    public ResponseData fetchAllData(String startDate){
        if (startDate != null) {
            return fetchData("/data/BBEX3/D..EUR.BB.AC.000?startPeriod="+startDate+"&detail=dataonly").bodyToMono(ResponseData.class).block();
        }
        return fetchData("/data/BBEX3/D..EUR.BB.AC.000?detail=dataonly").bodyToMono(ResponseData.class).block();
    }

    // Fetch latestData from API
    public ResponseData fetchLatestData(){
        String currentDate = LocalDate.now().toString();
        return fetchData("/data/BBEX3/D..EUR.BB.AC.000?startPeriod="+currentDate+"&endPeriod="+currentDate+"&detail=dataonly").bodyToMono(ResponseData.class).block();
    }

    // Extract available currencies from data
    public List<CurrencyData> extractAllCurrencies(ResponseData data){
       return data.getData().getStructure().getDimensions().getCurrencies().get(1).getValues();
    }

    // Extract available dates from data
    public List<Date> extractAllDates(ResponseData data){
        return data.getData().getStructure().getDimensions().getObservation().get(0).getValues();
    }

    // Extract Exchange rates and format result as map
    public Map<String, JsonNode> extractAllExchangeRates(ResponseData data){
        JsonNode series = data.getData().getDataSets().get(0).getSeries();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(series, new TypeReference<>(){});
    }


    // Add currencies to DB
    public ResponseEntity<Currency> addCurrencies(Currency currency){
        try{
            return new ResponseEntity<>(currencyRepo.save(currency), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add exchange rate data(currency, date and exchange rate) to DB
    public ResponseEntity<CurrencyExchangeRate> addExchangeRate(CurrencyExchangeRate exchangeRate){
        try{
            return new ResponseEntity<>(currencyExchangeRepo.save(exchangeRate), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Initialize currencies table in DB
    public void initializeCurrenciesTable(ResponseData data){
        List<CurrencyData> currencies = this.extractAllCurrencies(data);
        currencies.stream().map(CurrencyData::getId).forEach(value -> addCurrencies (new Currency(value)));
    }

    //Fill Exchange Rate Table in DB
    public void fillExchangeRateTable(ResponseData data){
        List<CurrencyData> currencies = this.extractAllCurrencies(data);
        List<Date> dates = this.extractAllDates(data);
        Map<String, JsonNode> allExchangeRates = this.extractAllExchangeRates(data);
        int currencyIndex = 0;

        for (JsonNode value: allExchangeRates.values()) {
            int dateIndex = 0;
            CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
            CurrencyExchangeRateId currencyExchangeRateId = new CurrencyExchangeRateId();
            currencyExchangeRateId.setCurrency(new Currency(currencies.get(currencyIndex).getId()));

            if(value.get("observations") == null){
                currencyIndex++;
                continue;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode oneCurrencyRates = value.get("observations");
            Map<String, List<Float>> oneCurrencyRatesMap = mapper.convertValue(oneCurrencyRates, new TypeReference<Map<String, List<Float>>>(){});

            for (List<Float> v: oneCurrencyRatesMap.values()) {

                if(v.get(0) == null){
                    dateIndex++;
                    continue;
                }

                float exchangeRate = withBigDecimal(v.get(0), 2);
                currencyExchangeRateId.setDate(dates.get(dateIndex).getId());
                currencyExchangeRate.setCurrencyExchangeRateId(currencyExchangeRateId);
                currencyExchangeRate.setExchange_rate(exchangeRate);
                this.addExchangeRate(currencyExchangeRate);
                dateIndex++;
            }
            currencyIndex++;
        }


    }

    // add try catch?
    // Initialize database(currencies table and exchange rate table)
    public ResponseEntity<DatabaseMessage> initializeDB(String startDate){
        try {
            ResponseData allData = this.fetchAllData(startDate);
            this.initializeCurrenciesTable(allData);
            this.fillExchangeRateTable(allData);
            DatabaseMessage message = new DatabaseMessage("Database initialization done!");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch(Exception e){
            DatabaseMessage message = new DatabaseMessage("Please provide the correct URL format");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }


    }

    // Add latest data available to DB
    public void addLatestDataToDB(){
        ResponseData latestData = this.fetchLatestData();
        this.fillExchangeRateTable(latestData);
    }

    // Get all currencies from DB
    public ResponseEntity<List<String>> getAllCurrencies(){
        try{
            List<String> currencies = currencyRepo.findAll().stream().map(Currency::getCurrencyId).collect(Collectors.toList());
            if(currencies.isEmpty()){
                return new ResponseEntity<>(currencies, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(currencies, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all exchange rates data available from DB
    public ResponseEntity<List<Map<String, Map<String, Float>>>> getAllExchangeRates(){
        try{
            List<CurrencyExchangeRate> exchangeRates = currencyExchangeRepo.findAll();
            List<Map<String, Map<String, Float>>> formattedResult = formatResult(exchangeRates);
            if(exchangeRates.isEmpty()){
                return new ResponseEntity<>(formattedResult, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(formattedResult, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all exchange rates data available at a given period from DB
    public ResponseEntity<List<Map<String, Map<String, Float>>>> getExchangeRatesByDate(String date){
        try{
            List<CurrencyExchangeRate> exchangeRates = currencyExchangeRepo.findByCurrencyExchangeRateIdDate(date);
            List<Map<String, Map<String, Float>>> formattedResult = formatResult(exchangeRates);
            if(exchangeRates.isEmpty()){
                return new ResponseEntity<>(formattedResult, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(formattedResult, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Convert certain amount of given currency on a given date to Euro
    public ResponseEntity<ConversionResult> convertToEuro(CurrencyConversionData conversionData){
        try{
            CurrencyExchangeRate exchangeRate = currencyExchangeRepo.findByCurrencyExchangeRateId(new CurrencyExchangeRateId(new Currency(conversionData.getCurrency()), conversionData.getDate()));
            if(exchangeRate == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String convertedAmount = conversionData.convertAmount(exchangeRate.getExchange_rate());
            return new ResponseEntity<>(new ConversionResult(convertedAmount), HttpStatus.OK);
            //return new ResponseEntity<>(conversionData.convertAmount(exchangeRate.getExchange_rate())+"€", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Schedule cron job on 4PM every day to update DB with the latest data from API
    @Scheduled(cron="0 0 16 * * ?", zone="UTC")
    public void updateDB() {
        this.addLatestDataToDB();
    }


    // Format exchange rate data received from DB to List of Maps
    public List<Map<String, Map<String, Float>>> formatResult(List<CurrencyExchangeRate> input){
        List<String> currencies = currencyRepo.findAll().stream().map(Currency::getCurrencyId).collect(Collectors.toList());
        Map<String, Map<String, Float>> currencyDataMap = new TreeMap<>();
        List<Map<String, Map<String, Float>>> finalFormattedResult = new ArrayList<>();
        List<Map<String, Map<String, Float>>> formattedInput = input.stream().map(this::formatToMap).collect(Collectors.toList());
        for (String currency: currencies){
            Map<String, Float> dateRateMap = new TreeMap<>();
            for(Map<String, Map<String, Float>> element: formattedInput){
                if (element.containsKey(currency)){
                    Map<String, Float> currencyData = element.get(currency);
                    Float rate = currencyData.get(currencyData.keySet().toArray()[0]);
                    String date = currencyData.keySet().toArray()[0].toString();
                    dateRateMap.put(date, rate);
                }
            }
            currencyDataMap.put(currency, dateRateMap);

        }
        finalFormattedResult.add(currencyDataMap);
        return finalFormattedResult;
    }

    // Format one currency/date/rate data received from DB to Map
    public Map<String, Map<String, Float>> formatToMap(CurrencyExchangeRate currencyExchangeRate){
        Map<String, Float> ERByDate = new HashMap<>(){
            {
                put(currencyExchangeRate.getCurrencyExchangeRateId().getDate(), currencyExchangeRate.getExchange_rate());
            }
        };
        Map<String, Map<String, Float>> result = new HashMap<>()
        {
            {
                put(currencyExchangeRate.getCurrencyExchangeRateId().getCurrency().getCurrencyId(), ERByDate);
            }
        };
        return result;
    }


    public static float withBigDecimal(float value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
}
