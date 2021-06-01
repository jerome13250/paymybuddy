package com.openclassrooms.paymybuddy.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.BankTransactionRepository;

@Service
public class BankTransactionServiceImpl implements BankTransactionService {

	Logger logger = LoggerFactory.getLogger(BankTransactionServiceImpl.class);

	@Autowired
	private BankTransactionRepository BankTransactionRepository;
	@Autowired
	private UserService userService;
	
	@Override
	public void create(BankTransaction BankTransaction) {
		logger.debug("Calling create(BankTransaction BankTransaction)");
		User currentUser = userService.getCurrentUser();
		
		BankTransaction.setBankaccountnumber(currentUser.getBankaccountnumber());
		BankTransaction.setDatetime(LocalDateTime.now());
		BankTransaction.setUser(currentUser);
				
		BankTransactionRepository.save(BankTransaction);
	}
	

}
