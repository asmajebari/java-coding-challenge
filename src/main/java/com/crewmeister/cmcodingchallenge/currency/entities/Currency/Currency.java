package com.crewmeister.cmcodingchallenge.currency.entities.Currency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="currency")
@Entity
public class Currency{

    @Id
    @Column(name = "currency_id")
    private String currencyId;

    public Currency() {
    }

    public Currency(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }


}
