package com.crewmeister.cmcodingchallenge.currency;

import com.crewmeister.cmcodingchallenge.currency.dto.*;
import com.crewmeister.cmcodingchallenge.currency.dto.ResponseData;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRate;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRateId;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyExchangeRateRepo;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

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

    public ResponseData getAllData(String startDate){
        if (startDate != null) {
            System.out.println(startDate);
            return fetchData("/data/BBEX3/D..EUR.BB.AC.000?startPeriod="+startDate+"&detail=dataonly").bodyToMono(ResponseData.class).block();
        }
        System.out.println("all");
        return fetchData("/data/BBEX3/D..EUR.BB.AC.000?detail=dataonly").bodyToMono(ResponseData.class).block();
    }

    public List<CurrencyData> getAllCurrencies(ResponseData data){
       return data.getData().getStructure().getDimensions().getCurrencies().get(1).getValues();
    }

    public List<Date> getAllDates(ResponseData data){
        return data.getData().getStructure().getDimensions().getObservation().get(0).getValues();
    }

    public Map<String, JsonNode> getAllExchangeRates(ResponseData data){
        JsonNode series = data.getData().getDataSets().get(0).getSeries();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, JsonNode> seriesMap = mapper.convertValue(series, new TypeReference<Map<String, JsonNode>>(){});
        return seriesMap;
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
        List<CurrencyData> currencies = this.getAllCurrencies(data);
        currencies.stream().map(CurrencyData::getId).forEach(value -> addCurrencies (new Currency(value)));
    }

    public void initializeExchangeRateTable(ResponseData data){
        List<CurrencyData> currencies = this.getAllCurrencies(data);
        List<Date> dates = this.getAllDates(data);
        Map<String, JsonNode> allExchangeRates = this.getAllExchangeRates(data);
        //System.out.println(allExchangeRates.get(allExchangeRates.keySet().toArray()[0]));
        int currencyIndex = 0;
        NumberFormat formatter = new DecimalFormat("#0.00");
        for (JsonNode value: allExchangeRates.values()) {
            int dateIndex = 0;
            CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
            CurrencyExchangeRateId currencyExchangeRateId = new CurrencyExchangeRateId();
            //set currency id in composite id
            currencyExchangeRateId.setCurrency(new Currency(currencies.get(currencyIndex).getId()));
            System.out.println(currencyExchangeRateId.getCurrency().getCurrencyId());
            //add currency to object
            if(value.get("observations") == null){
                currencyIndex++;
                continue;
            }
            JsonNode oneCurrencyRates = value.get("observations");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<Float>> oneCurrencyRatesMap = mapper.convertValue(oneCurrencyRates, new TypeReference<Map<String, List<Float>>>(){});
            for (List<Float> v: oneCurrencyRatesMap.values()) {
                if(v.get(0) == null){
                    dateIndex++;
                    continue;
                }
                //float exchangeRate = Float.parseFloat(formatter.format(v.get(0)));
                float exchangeRate = withBigDecimal(v.get(0), 2);
                //add date id to composite id
                currencyExchangeRateId.setDate(dates.get(dateIndex).getId());
                currencyExchangeRate.setCurrencyExchangeRateId(currencyExchangeRateId);
                currencyExchangeRate.setExchange_rate(exchangeRate);
                this.addExchangeRate(currencyExchangeRate);
                dateIndex++;
            }
            currencyIndex++;
        }


    }

    public String initializeDB(String startDate){
        ResponseData allData = this.getAllData(startDate);
        initializeCurrenciesTable(allData);
        initializeExchangeRateTable(allData);
        return "Database intialization done!";
    }


    public static float withBigDecimal(float value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
}
