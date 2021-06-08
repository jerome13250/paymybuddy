package com.openclassrooms.paymybuddy.service.interfaces;

import java.math.BigDecimal;
import java.util.Map;

import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

public interface UserTransactionService {
	void create(UserTransaction bankTransaction, Map<String, BigDecimal> feesMap);
	Paged<UserTransaction> getCurrentUserUserTransactionPage(int pageNumber, int size);
}
