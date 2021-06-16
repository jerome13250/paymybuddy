package com.openclassrooms.paymybuddy.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.util.Currency;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO object that contains the informations from form page for a transaction between a user and a bank.
 * @author jerome
 *
 */

@Getter
@Setter
public class BankTransactionFormDTO {
	
	@NotBlank
	private String getOrSendRadioOptions;
	
	@NotNull
	@Positive
	@Max(99999999) //due to Mysql setting : DECIMAL(10,2) to store amount
	private BigDecimal amount;
	
	//validation is done in BankTransactionController
	private Currency currency;
	
}
