package com.openclassrooms.paymybuddy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.openclassrooms.paymybuddy.model.Currency;

public interface CurrencyRepository  extends JpaRepository<Currency, Long>{
	
}
