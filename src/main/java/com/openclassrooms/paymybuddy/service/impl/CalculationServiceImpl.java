package com.openclassrooms.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.service.interfaces.CalculationService;

import lombok.Setter;

/**
 * Implementation of service that allows calculations for fees and currencies.
 * @author jerome
 *
 */

@Setter
@Service
public class CalculationServiceImpl implements CalculationService {

	//get the currencies conversion array as a Map : https://www.baeldung.com/spring-value-annotation#using-value-with-maps 
	@Value("#{${currencies.conversionMap}}")
	private Map <String, BigDecimal> currenciesConversionMap; //protected for unit test 
	MathContext mc = new MathContext(15,RoundingMode.FLOOR); //precision de calcul a 15 chiffres alors qu'on ne peut monter qu'a 10 (MySQL DECIMAL(10.2) ) => OK

	//get the fee value from application.properties
	@Value("${fee.value}")
	private BigDecimal feeValue;
	
	@Override
	public BigDecimal sumCurrencies(BigDecimal amount, Currency currency, BigDecimal amountToSum, Currency currencyOfAmountToSum){
		if ( currency.equals(currencyOfAmountToSum) ) {
			return amount.add(amountToSum,mc).setScale(2,RoundingMode.FLOOR);
		}	
		else {			
			return amount.add(convertCurrency(amountToSum, currencyOfAmountToSum, currency), mc);
		}
	}
	
	
	private BigDecimal convertCurrency(BigDecimal amount, Currency initialCurrency, Currency targetCurrency){		
		BigDecimal conversionRate = currenciesConversionMap.get(initialCurrency.getCurrencyCode() + targetCurrency.getCurrencyCode());
		return amount.multiply(conversionRate,mc).setScale(2,RoundingMode.FLOOR);
		
	}
	
	@Override
	public Map<String, BigDecimal> calculateFees(BigDecimal initialAmount){
		
		Map<String, BigDecimal> feesMap = new HashMap<>();
		BigDecimal fees = initialAmount.multiply(feeValue, mc).setScale(2,RoundingMode.UP);
		feesMap.put("fees",fees);
		BigDecimal finalAmount = initialAmount.subtract(fees, mc);
		feesMap.put("finalAmount",finalAmount);
		
		return feesMap;
	}
	
	
	
	
}
