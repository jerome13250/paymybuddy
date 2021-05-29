package com.openclassrooms.paymybuddy.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class CalculationCurrencyService {

	@Value("#{${currencies.conversionMap}}")
	protected Map <String, BigDecimal> currenciesConversionMap; //protected for unit test 
	MathContext mc = new MathContext(15,RoundingMode.FLOOR); //precision de calcul a 15 chiffres alors qu'on ne peut monter qu'a 10 (MySQL DECIMAL(10.2) ) => OK

	BigDecimal sumCurrencies(BigDecimal Amount, Currency currency, BigDecimal amountToSum, Currency currencyOfAmountToSum){
		if ( currency.equals(currencyOfAmountToSum) ) {
			return Amount.add(amountToSum,mc).setScale(2,RoundingMode.FLOOR);
		}	
		else {			
			return Amount.add(convertCurrency(amountToSum, currencyOfAmountToSum, currency), mc);
		}
	}
	
	BigDecimal convertCurrency(BigDecimal Amount, Currency initialCurrency, Currency targetCurrency){
		
		BigDecimal conversionRate = currenciesConversionMap.get(initialCurrency.getCurrencyCode() + targetCurrency.getCurrencyCode());
		return Amount.multiply(conversionRate,mc).setScale(2,RoundingMode.FLOOR);
		
	}
	
	
}
