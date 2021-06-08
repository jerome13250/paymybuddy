package com.openclassrooms.paymybuddy.service.interfaces;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public interface CalculationService {

	/**
	 * Function to sum two amounts of different currencies using the conversion Map : currenciesConversionMap.
	 * 
	 * @param amount initial amount of the user
	 * @param currency the currency of the user, <b>it will also be the currency of the returned sum.</b>
	 * @param amountToSum the amount to sum.
	 * @param currencyOfAmountToSum currency of the amount to sum, if different from parameter "currency" it will need conversion.
	 * @return the calculated sum in initial currency
	 */
	BigDecimal sumCurrencies(BigDecimal amount, Currency currency, BigDecimal amountToSum,
			Currency currencyOfAmountToSum);

	/**
	 * This function calculates fees using fee.value from application.properties.
	 * 
	 * @param initialAmount, initial amount of money on which we need to substract fees.
	 * @return a Map containing "fees" and "finalAmount"
	 */
	Map<String, BigDecimal> calculateFees(BigDecimal initialAmount);

}