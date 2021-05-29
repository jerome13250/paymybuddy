package com.openclassrooms.paymybuddy.config;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class CurrenciesAllowed {

	List <Currency> currenciesAllowedList;

	@Autowired
	public CurrenciesAllowed(@Value("${currencies.allowed}") String[] allowedCurrencyCodes) {
		
		this.currenciesAllowedList = new ArrayList<>();
		for(String code: allowedCurrencyCodes) {
			this.currenciesAllowedList.add(Currency.getInstance(code));
		}
	}
	
	
	
	
}
