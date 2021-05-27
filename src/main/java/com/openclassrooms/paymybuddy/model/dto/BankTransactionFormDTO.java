package com.openclassrooms.paymybuddy.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.openclassrooms.paymybuddy.model.Currency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankTransactionFormDTO {
	
	@NotBlank
	private String getOrSendRadioOptions;
	
	@NotNull
	@Positive
	private BigDecimal amount;
	
	
	private Currency currency;
	
}
