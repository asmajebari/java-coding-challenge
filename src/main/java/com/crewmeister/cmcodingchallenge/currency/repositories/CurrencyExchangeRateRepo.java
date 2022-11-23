package com.crewmeister.cmcodingchallenge.currency.repositories;

import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRate;
import com.crewmeister.cmcodingchallenge.currency.entities.CurrencyExchangeRate.CurrencyExchangeRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyExchangeRateRepo extends JpaRepository<CurrencyExchangeRate, CurrencyExchangeRateId> {

     List<CurrencyExchangeRate> findByCurrencyExchangeRateIdDate(String date);
     CurrencyExchangeRate findByCurrencyExchangeRateId(CurrencyExchangeRateId id);

}
