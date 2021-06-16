package com.openclassrooms.paymybuddy.service.interfaces;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

/**
 * Service that allows handling the bank transactions.
 * @author jerome
 *
 */

public interface BankTransactionService {
	void create(BankTransaction bankTransaction);
	void deleteAll();
	Paged<BankTransaction> getCurrentUserBankTransactionPage(int pageNumber, int size);
	
}
