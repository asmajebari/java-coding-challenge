package com.crewmeister.cmcodingchallenge.currency;

import com.crewmeister.cmcodingchallenge.currency.dto.CurrencyConversionData;
import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRateId;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyExchangeRateRepo;
import com.crewmeister.cmcodingchallenge.currency.repositories.CurrencyRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepo currencyRepo;

    @Mock
    CurrencyExchangeRateRepo exchangeRateRepo;

    private CurrencyService currencyService;
    @BeforeEach
    void setUp(){
        this.currencyService = new CurrencyService(currencyRepo, exchangeRateRepo);
    }


    @Test
    void canGetAllCurrencies() {
        this.currencyService.getAllCurrencies();
        verify(currencyRepo).findAll();
    }

    @Test
    void canGetAllExchangeRates() {
        this.currencyService.getAllExchangeRates();
        verify(exchangeRateRepo).findAll();
    }

    @Test
    void canGetExchangeRatesByDate() {
        this.currencyService.getExchangeRatesByDate("2022-04-04");
        verify(exchangeRateRepo).findByCurrencyExchangeRateIdDate("2022-04-04");
    }

}