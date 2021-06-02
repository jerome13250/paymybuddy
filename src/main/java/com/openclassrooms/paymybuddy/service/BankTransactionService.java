package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

public interface BankTransactionService {
	void create(BankTransaction bankTransaction);
	Paged<BankTransaction> getCurrentUserBankTransactionPage(int pageNumber, int size);
}
