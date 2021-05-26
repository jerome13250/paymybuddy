package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.paymybuddy.model.BankTransaction;

public interface BankTransactionRepository  extends JpaRepository<BankTransaction, Long>{
	
}
