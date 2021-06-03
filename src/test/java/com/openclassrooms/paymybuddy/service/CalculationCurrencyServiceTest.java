package com.openclassrooms.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.openclassrooms.paymybuddy.service.impl.CalculationCurrencyService;

class CalculationCurrencyServiceTest {

	@Test
	void testAddTwoSameCurrencies() {
		// Arrange
		BigDecimal a = new BigDecimal("2.38");
		Currency cur1 = Currency.getInstance("EUR");
		BigDecimal b = new BigDecimal("3.54");
		Currency cur2 = Currency.getInstance("EUR");
		CalculationCurrencyService calculationCurrencyService = new CalculationCurrencyService();

		// Act
		BigDecimal result = calculationCurrencyService.sumCurrencies(a, cur1, b, cur2);

		// Assert
		assertEquals(new BigDecimal("5.92"),result);
	}
	
	@Test
	void testAddTwoDifferentCurrencies() {
		// Arrange
		BigDecimal a = new BigDecimal("2.38");
		Currency cur1 = Currency.getInstance("EUR");
		BigDecimal b = new BigDecimal("3.54");
		Currency cur2 = Currency.getInstance("USD");
		CalculationCurrencyService calculationCurrencyService = new CalculationCurrencyService();
		
		Map <String, BigDecimal> mapCurConversion = new HashMap<>();
		mapCurConversion.put("USDEUR", new BigDecimal("0.82"));
		calculationCurrencyService.currenciesConversionMap = mapCurConversion;
		
		// Act
		BigDecimal result = calculationCurrencyService.sumCurrencies(a, cur1, b, cur2);

		// Assert
		assertEquals(new BigDecimal("5.28"),result);
	}
	
	
	
	
}
