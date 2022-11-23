package com.crewmeister.cmcodingchallenge.currency;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRate;
import com.crewmeister.cmcodingchallenge.currency.models.CurrencyConversionData;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api")
public class CurrencyController {
    @Autowired
    CurrencyService currencyService;
/*
    @GetMapping("/currencies")
    public ResponseEntity<ArrayList<CurrencyConversionRates>> getCurrencies() {
        ArrayList<CurrencyConversionRates> currencyConversionRates = new ArrayList<CurrencyConversionRates>();
        currencyConversionRates.add(new CurrencyConversionRates(2.5));

        return new ResponseEntity<ArrayList<CurrencyConversionRates>>(currencyConversionRates, HttpStatus.OK);
    }

 */
/*
    @GetMapping("/currencies/australia")
    public ResponseData getCurrency(){
        return this.currencyService.getCurrency();
    }

    */

/*
    @GetMapping("/currencies/all")
    public List<Value__1> getAllData(){
        return this.currencyService.getAllDates();
    }

 */
/*
    @GetMapping("/currencies/rates")
    public Map getAllRates(){
        return this.currencyService.getAllExchangeRates();
    }

 */
    @GetMapping("/init")
    public ResponseEntity<String> initDB(@RequestParam(required = false) String startDate){
       return this.currencyService.initializeDB(startDate);
    }

    @GetMapping("/currencies/all")
    public ResponseEntity<List<String>> getCurrencies() {
        return this.currencyService.getAllCurrencies();
    }

    @GetMapping("/currencies/exchange-rates")
    public ResponseEntity<List<CurrencyExchangeRate>> getExchangeRates() {
        return this.currencyService.getAllExchangeRates();
    }


    @GetMapping("/currencies/exchange-rates/search")
    public ResponseEntity<List<CurrencyExchangeRate>> getExchangeRatesByDate(@RequestParam String date) {
        return this.currencyService.getExchangeRatesByDate(date);
    }

    @PostMapping("/currencies/conversion")
    public ResponseEntity<String> convertToEuro(@RequestBody CurrencyConversionData conversionData){
       return this.currencyService.convertToEuro(conversionData);
    }
/*
    @PostMapping("/currencies/addRates")
    public ResponseEntity<CurrencyExchangeRate> save(@RequestBody CurrencyExchangeRate exchangeRate){
        try{
            return new ResponseEntity<>(currencyExchangeRepo.save(exchangeRate), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */

    @Autowired
    CurrencyRepo currencyRepo;
    @PostMapping("/currencies/addCurrencies")
    public ResponseEntity<Currency> save(@RequestBody Currency currency){
        try{
            return new ResponseEntity<>(currencyRepo.save(currency), HttpStatus.CREATED);
        } catch(Exception e){
            System.out.print(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
