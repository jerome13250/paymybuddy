package com.openclassrooms.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.repositories.UserTransactionRepository;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.service.interfaces.UserTransactionService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

@Service
public class UserTransactionServiceImpl implements UserTransactionService {

	Logger logger = LoggerFactory.getLogger(UserTransactionServiceImpl.class);

	@Autowired
	private UserTransactionRepository userTransactionRepository;
	@Autowired
	private UserService userService;
	
	@Override
	public void create(UserTransaction userTransaction) {
		logger.debug("Calling create(UserTransaction UserTransaction)");
		User currentUser = userService.getCurrentUser();
		
		userTransaction.setDatetime(LocalDateTime.now());
		userTransaction.setUserSource(currentUser);
		//TODO: faire un service FeeCalculation:
		BigDecimal fees = userTransaction.getAmount().divide(new BigDecimal("100"), RoundingMode.FLOOR);
		userTransaction.setFees(fees);
		userTransaction.setAmount(userTransaction.getAmount().subtract(fees));
		
		userTransactionRepository.save(userTransaction);
	}

	@Override
	public Paged<UserTransaction> getCurrentUserUserTransactionPage(int pageNumber, int size) {
		
		PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserTransaction> page = userTransactionRepository.findUserTransactionByUserId(userService.getCurrentUser().getId(),request);
        return new Paged<>(page, Paging.of(page.getTotalPages(), pageNumber));//, size));
	}
}