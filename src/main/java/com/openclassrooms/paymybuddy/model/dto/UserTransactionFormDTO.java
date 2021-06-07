package com.openclassrooms.paymybuddy.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.openclassrooms.paymybuddy.model.User;

import java.util.Currency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTransactionFormDTO {
	
	//validation is done in UserTransactionController
	private User userDestination;
	
	@NotNull
	@Positive
	@Max(99999999) //due to Mysql setting : DECIMAL(10,2) to store amount
	private BigDecimal amount;
	
	//validation is done in UserTransactionController
	private Currency currency;
	
}
