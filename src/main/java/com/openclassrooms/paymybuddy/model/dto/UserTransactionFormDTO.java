package com.openclassrooms.paymybuddy.model.dto;

import java.math.BigDecimal;
import java.util.Currency;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO object that contains the informations from form page for a transaction between two users.
 * @author jerome
 *
 */

@Getter
@Setter
public class UserTransactionFormDTO {
	
	@NotNull
	//A cross-record validation is also done in UserTransactionController
	private Long userDestinationId;
	
	@NotNull
	@Positive
	@Max(99999999) //due to Mysql setting : DECIMAL(10,2) to store amount
	private BigDecimal amount;
	
	@NotNull
	//A cross-record validation is also done in UserTransactionController
	private Currency currency;
	
}
