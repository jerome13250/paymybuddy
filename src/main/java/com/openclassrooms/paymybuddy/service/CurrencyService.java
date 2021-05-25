package com.openclassrooms.paymybuddy.service;

import java.util.List;

import com.openclassrooms.paymybuddy.model.Currency;

public interface CurrencyService {
	List<Currency> findAll();
}
