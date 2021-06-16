package com.openclassrooms.paymybuddy.service.interfaces;

import java.math.BigDecimal;
import java.util.Map;

import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

/**
 * Service that allows handling the transactions between users.
 * @author jerome
 *
 */

public interface UserTransactionService {
	void create(UserTransaction userTransaction, Map<String, BigDecimal> feesMap);
	Paged<UserTransaction> getCurrentUserUserTransactionPage(int pageNumber, int size);
}
