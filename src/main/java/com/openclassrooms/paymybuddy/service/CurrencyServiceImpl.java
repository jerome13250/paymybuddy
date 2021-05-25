package com.openclassrooms.paymybuddy.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.model.Currency;
import com.openclassrooms.paymybuddy.repositories.CurrencyRepository;

@Service
public class CurrencyServiceImpl implements CurrencyService {

	Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

	@Autowired
	private CurrencyRepository currencyRepository;

	@Override
	public List<Currency> findAll() {
		return currencyRepository.findAll();
	}
	
	
}
