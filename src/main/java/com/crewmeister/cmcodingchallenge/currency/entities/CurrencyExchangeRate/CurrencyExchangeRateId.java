package com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate;

import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;

import javax.persistence.*;
import java.io.Serializable;
@Embeddable
public class CurrencyExchangeRateId implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(name="currency_id", nullable=false)
    private Currency currency;

    @Column(name="date", nullable = false)
    private String date;

    public CurrencyExchangeRateId() {
    }

    public CurrencyExchangeRateId(Currency currency, String date) {
        this.currency = currency;
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
