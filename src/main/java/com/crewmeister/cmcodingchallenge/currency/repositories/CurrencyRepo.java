package com.crewmeister.cmcodingchallenge.currency.repositories;



import com.crewmeister.cmcodingchallenge.currency.entities.Currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepo extends JpaRepository<Currency, String>{

}
