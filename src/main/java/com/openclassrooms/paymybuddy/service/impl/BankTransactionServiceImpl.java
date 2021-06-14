package com.openclassrooms.paymybuddy.service.impl;

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
import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

@Service
public class BankTransactionServiceImpl implements BankTransactionService {

	Logger logger = LoggerFactory.getLogger(BankTransactionServiceImpl.class);

	@Autowired
	private BankTransactionRepository bankTransactionRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private LocalDateTimeService localDateTimeServiceImpl;
	@Autowired
	private PagingService pagingService;
	
	@Override
	public void create(BankTransaction bankTransaction) {
		logger.debug("Calling create(BankTransaction BankTransaction)");
		User currentUser = userService.getCurrentUser();
		
		bankTransaction.setBankaccountnumber(currentUser.getBankaccountnumber());
		bankTransaction.setDatetime(localDateTimeServiceImpl.now());
		bankTransaction.setUser(currentUser);
				
		bankTransactionRepository.save(bankTransaction);
	}

	@Override
	public Paged<BankTransaction> getCurrentUserBankTransactionPage(int pageNumber, int size) {
		
		PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BankTransaction> page = bankTransactionRepository.findBankTransactionByUserId(userService.getCurrentUser().getId(),request);
        return new Paged<>(page, pagingService.of(page.getTotalPages(), pageNumber));
		
	}
	
	@Override
	public void deleteAll() {
		bankTransactionRepository.deleteAll();
	}
	

}
