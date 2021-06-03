package com.openclassrooms.paymybuddy.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.BankTransactionRepository;
import com.openclassrooms.paymybuddy.service.interfaces.BankTransactionService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

@Service
public class BankTransactionServiceImpl implements BankTransactionService {

	Logger logger = LoggerFactory.getLogger(BankTransactionServiceImpl.class);

	@Autowired
	private BankTransactionRepository bankTransactionRepository;
	@Autowired
	private UserService userService;
	
	@Override
	public void create(BankTransaction bankTransaction) {
		logger.debug("Calling create(BankTransaction BankTransaction)");
		User currentUser = userService.getCurrentUser();
		
		bankTransaction.setBankaccountnumber(currentUser.getBankaccountnumber());
		bankTransaction.setDatetime(LocalDateTime.now());
		bankTransaction.setUser(currentUser);
				
		bankTransactionRepository.save(bankTransaction);
	}

	@Override
	public Paged<BankTransaction> getCurrentUserBankTransactionPage(int pageNumber, int size) {
		
		PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BankTransaction> page = bankTransactionRepository.findBankTransactionByUserId(userService.getCurrentUser().getId(),request);
        return new Paged<>(page, Paging.of(page.getTotalPages(), pageNumber, size));
		
	}
	

}
