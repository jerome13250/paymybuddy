package com.openclassrooms.paymybuddy.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserAmountException extends Exception {
	
	private String errorCode;
	private String defaultMessage;
	

}
