package com.crewmeister.cmcodingchallenge.currency;

import com.crewmeister.cmcodingchallenge.currency.dto.*;
import com.crewmeister.cmcodingchallenge.currency.dto.ResponseData;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRate;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRateId;
import com.crewmeister.cmcodingchallenge.currency.models.CurrencyConversionData;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyExchangeRateRepo;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    @Autowired
    private WebClient webClient;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    CurrencyExchangeRateRepo currencyExchangeRepo;


    public WebClient.ResponseSpec fetchData(String URI){
       return webClient.get().uri(URI).retrieve();
    }

    public ResponseData fetchAllData(String startDate){
        if (startDate != null) {
            return fetchData("/data/BBEX3/D..EUR.BB.AC.000?startPeriod="+startDate+"&detail=dataonly").bodyToMono(ResponseData.class).block();
        }
        return fetchData("/data/BBEX3/D..EUR.BB.AC.000?detail=dataonly").bodyToMono(ResponseData.class).block();
    }

    public ResponseData fetchLatestData(){
        String currentDate = LocalDate.now().toString();
        return fetchData("/data/BBEX3/D..EUR.BB.AC.000?startPeriod="+currentDate+"&endPeriod="+currentDate+"&detail=dataonly").bodyToMono(ResponseData.class).block();
    }
    public List<CurrencyData> extractAllCurrencies(ResponseData data){
       return data.getData().getStructure().getDimensions().getCurrencies().get(1).getValues();
    }

    public List<Date> extractAllDates(ResponseData data){
        return data.getData().getStructure().getDimensions().getObservation().get(0).getValues();
    }

    public Map<String, JsonNode> extractAllExchangeRates(ResponseData data){
        JsonNode series = data.getData().getDataSets().get(0).getSeries();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(series, new TypeReference<>(){});
    }


    public ResponseEntity<Currency> addCurrencies(Currency currency){
        try{
            return new ResponseEntity<>(currencyRepo.save(currency), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<CurrencyExchangeRate> addExchangeRate(CurrencyExchangeRate exchangeRate){
        try{
            return new ResponseEntity<>(currencyExchangeRepo.save(exchangeRate), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void initializeCurrenciesTable(ResponseData data){
        List<CurrencyData> currencies = this.extractAllCurrencies(data);
        currencies.stream().map(CurrencyData::getId).forEach(value -> addCurrencies (new Currency(value)));
    }

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

    public ResponseEntity<String> initializeDB(String startDate){
        ResponseData allData = this.fetchAllData(startDate);
        this.initializeCurrenciesTable(allData);
        this.fillExchangeRateTable(allData);
        JSONObject message = new JSONObject();
        message.put("message", "Database initialization done!");
        return new ResponseEntity<>(message.toString(), HttpStatus.OK);
    }

    public void addLatestDataToDB(){
        ResponseData latestData = this.fetchLatestData();
        this.fillExchangeRateTable(latestData);
    }

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

    public ResponseEntity<List<CurrencyExchangeRate>> getAllExchangeRates(){
        try{
            List<CurrencyExchangeRate> exchangeRates = currencyExchangeRepo.findAll();
            //formatResult(exchangeRates);
            if(exchangeRates.isEmpty()){
                return new ResponseEntity<>(exchangeRates, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(exchangeRates, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<CurrencyExchangeRate>> getExchangeRatesByDate(String date){
        try{
            List<CurrencyExchangeRate> exchangeRates = currencyExchangeRepo.findByCurrencyExchangeRateIdDate(date);
            if(exchangeRates.isEmpty()){
                return new ResponseEntity<>(exchangeRates, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(exchangeRates, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public double convertAmount(double amount, float exchangeRate){
        return amount/exchangeRate;
    }
    public ResponseEntity<String> convertToEuro(CurrencyConversionData conversionData){
        try{
            CurrencyExchangeRate exchangeRate = currencyExchangeRepo.findByCurrencyExchangeRateId(new CurrencyExchangeRateId(new Currency(conversionData.getCurrency()), conversionData.getDate()));

            if(exchangeRate == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(this.convertAmount(conversionData.getAmount(), exchangeRate.getExchange_rate())+"€", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Scheduled(cron="0 0 16 * * ?", zone="UTC")
    public void updateDB() {
        this.addLatestDataToDB();
    }
/*
    public void formatResult(List<CurrencyExchangeRate> input){
        System.out.println("in!");
        /*
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> result = mapper.convertValue(input, new TypeReference<>(){});


        List<String> result = input.stream().map(CurrencyExchangeRate::toJsonString).collect(Collectors.toList());
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Map<String, Float>>> map = mapper.readValue(json, Map.class);
       // System.out.println(result.get(result.keySet().toArray()[0]));
        System.out.println(result);
    }

*/
    public static float withBigDecimal(float value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
}
