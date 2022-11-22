package com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
@Table(name="currency_rate")
@Entity
public class CurrencyExchangeRate {
    @EmbeddedId
    private CurrencyExchangeRateId currencyExchangeRateId;

    @Column(name="exchange_rate")
    private float exchangeRate;


    public CurrencyExchangeRate() {
    }

    public CurrencyExchangeRate(CurrencyExchangeRateId currencyExchangeRateId, float exchangeRate) {
        this.currencyExchangeRateId = currencyExchangeRateId;
        this.exchangeRate = exchangeRate;
    }

    public CurrencyExchangeRateId getCurrencyExchangeRateId() {
        return currencyExchangeRateId;
    }

    public void setCurrencyExchangeRateId(CurrencyExchangeRateId currencyExchangeRateId) {
        this.currencyExchangeRateId = currencyExchangeRateId;
    }

    public float getExchange_rate() {
        return exchangeRate;
    }

    public void setExchange_rate(float exchange_rate) {
        this.exchangeRate = exchange_rate;
    }
}
