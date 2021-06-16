package com.openclassrooms.paymybuddy.config;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * This class contains the allowed currencies list, values are read from application.properties file.
 * It is automatically loaded thanks to @Component.
 * 
 * @author jerome
 *
 */
@Getter
@Component
public class CurrenciesAllowed {

	List <Currency> currenciesAllowedList;
	
	/**
	 * Constructor that uses application.properties file to populate currenciesAllowedList.
	 * @param allowedCurrencyCodes list of allowed currencies in our application, comes from comma-separated values of the properties file
	 */
	@Autowired
	public CurrenciesAllowed(@Value("${currencies.allowed}") String[] allowedCurrencyCodes) {
		
		this.currenciesAllowedList = new ArrayList<>();
		for(String code: allowedCurrencyCodes) {
			this.currenciesAllowedList.add(Currency.getInstance(code));
		}
	}
	
	
	
	
}
