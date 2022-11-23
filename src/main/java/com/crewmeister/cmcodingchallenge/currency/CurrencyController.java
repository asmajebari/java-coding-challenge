package com.crewmeister.cmcodingchallenge.currency;
import com.crewmeister.cmcodingchallenge.currency.dto.ConversionResult;
import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyConversionData;
import com.crewmeister.cmcodingchallenge.currency.dto.DatabaseMessage;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api")
public class CurrencyController {
    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyRepo currencyRepo;

    // Initiliaze database with all data from api or starting from certain date
    @GetMapping("/init-database")
    public ResponseEntity<DatabaseMessage> initDB(@RequestParam(required = false) String startDate){
       return this.currencyService.initializeDB(startDate);
    }

    // Get all currencies available
    @GetMapping("/currencies/all")
    public ResponseEntity<List<String>> getCurrencies() {
        return this.currencyService.getAllCurrencies();
    }

    // Get all exchange rates from all currencies at all available times
    @GetMapping("/currencies/exchange-rates")
    public ResponseEntity<List<Map<String, Map<String, Float>>>> getExchangeRates() {
        return this.currencyService.getAllExchangeRates();
    }

    // Get all exchange rates from all currencies by date
    @GetMapping("/currencies/exchange-rates/search")
    public ResponseEntity<List<Map<String, Map<String, Float>>>> getExchangeRatesByDate(@RequestParam String date) {
        return this.currencyService.getExchangeRatesByDate(date);
    }

    // Convert certain amount of given currency on a given date to Euro
    @PostMapping("/currencies/conversion")
    public ResponseEntity<ConversionResult> convertToEuro(@RequestBody CurrencyConversionData conversionData){
       return this.currencyService.convertToEuro(conversionData);
    }

}
