package com.openclassrooms.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CalculationCurrencyService {

	@Value("#{${currencies.conversionMap}}")
	public Map <String, BigDecimal> currenciesConversionMap; //protected for unit test 
	MathContext mc = new MathContext(15,RoundingMode.FLOOR); //precision de calcul a 15 chiffres alors qu'on ne peut monter qu'a 10 (MySQL DECIMAL(10.2) ) => OK

	public BigDecimal sumCurrencies(BigDecimal amount, Currency currency, BigDecimal amountToSum, Currency currencyOfAmountToSum){
		if ( currency.equals(currencyOfAmountToSum) ) {
			return amount.add(amountToSum,mc).setScale(2,RoundingMode.FLOOR);
		}	
		else {			
			return amount.add(convertCurrency(amountToSum, currencyOfAmountToSum, currency), mc);
		}
	}
	
	BigDecimal convertCurrency(BigDecimal amount, Currency initialCurrency, Currency targetCurrency){
		
		BigDecimal conversionRate = currenciesConversionMap.get(initialCurrency.getCurrencyCode() + targetCurrency.getCurrencyCode());
		return amount.multiply(conversionRate,mc).setScale(2,RoundingMode.FLOOR);
		
	}
	
	
}
