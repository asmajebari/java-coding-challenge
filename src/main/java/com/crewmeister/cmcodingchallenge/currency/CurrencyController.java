package com.crewmeister.cmcodingchallenge.currency;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController()
@RequestMapping("/api")
public class CurrencyController {
    @Autowired
    CurrencyService currencyService;

    @GetMapping("/currencies")
    public ResponseEntity<ArrayList<CurrencyConversionRates>> getCurrencies() {
        ArrayList<CurrencyConversionRates> currencyConversionRates = new ArrayList<CurrencyConversionRates>();
        currencyConversionRates.add(new CurrencyConversionRates(2.5));

        return new ResponseEntity<ArrayList<CurrencyConversionRates>>(currencyConversionRates, HttpStatus.OK);
    }
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
    public String initDB(@RequestParam(required = false) String startDate){
       return this.currencyService.initializeDB(startDate);
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
