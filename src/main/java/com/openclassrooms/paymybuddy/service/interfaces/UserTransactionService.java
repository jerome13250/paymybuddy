package com.openclassrooms.paymybuddy.service.interfaces;

import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

public interface UserTransactionService {
	void create(UserTransaction bankTransaction);
	Paged<UserTransaction> getCurrentUserUserTransactionPage(int pageNumber, int size);
}
