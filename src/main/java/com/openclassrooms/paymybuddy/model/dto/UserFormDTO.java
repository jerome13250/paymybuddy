package com.openclassrooms.paymybuddy.model.dto;

import java.util.Currency;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.openclassrooms.paymybuddy.validation.PasswordEquality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordEquality
public class UserFormDTO {

	@NotBlank
	private String firstname;
	@NotBlank
	private String lastname;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String passwordconfirm;
	@NotBlank
	private String bankaccountnumber;
	private Currency currency;
	
	
}
