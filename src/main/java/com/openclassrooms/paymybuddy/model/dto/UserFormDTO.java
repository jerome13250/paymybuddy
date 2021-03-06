package com.openclassrooms.paymybuddy.model.dto;

import java.util.Currency;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.openclassrooms.paymybuddy.validation.PasswordEquality;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO object that contains the informations from form page for a user registration.
 * @author jerome
 *
 */

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
	@NotNull
	private Currency currency;
	
	
}
