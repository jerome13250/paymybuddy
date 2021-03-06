package com.openclassrooms.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.util.Map;

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
import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.service.interfaces.UserTransactionService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

/**
 * Implementation of service that allows handling the transactions between users.
 * @author jerome
 *
 */

@Service
public class UserTransactionServiceImpl implements UserTransactionService {

	Logger logger = LoggerFactory.getLogger(UserTransactionServiceImpl.class);

	@Autowired
	private UserTransactionRepository userTransactionRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private LocalDateTimeService localDateTimeServiceImpl;
	@Autowired
	private PagingService pagingService;
	
	@Override
	public void create(UserTransaction userTransaction, Map<String, BigDecimal> feesMap) {
		logger.debug("Calling create(UserTransaction UserTransaction)");
		User currentUser = userService.getCurrentUser();
		
		userTransaction.setDatetime(localDateTimeServiceImpl.now());
		userTransaction.setUserSource(currentUser);
		userTransaction.setFees(feesMap.get("fees"));
		userTransaction.setAmount(feesMap.get("finalAmount"));
		
		userTransactionRepository.save(userTransaction);
	}

	@Override
	public Paged<UserTransaction> getCurrentUserUserTransactionPage(int pageNumber, int size) {
		
		PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserTransaction> page = userTransactionRepository.findUserTransactionByUserId(userService.getCurrentUser().getId(),request);
        return new Paged<>(page, pagingService.of(page.getTotalPages(), pageNumber));
	}
}
