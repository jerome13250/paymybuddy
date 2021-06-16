package com.openclassrooms.paymybuddy.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception to handle user account invalid state after a transaction (with bank or between users).<br>
 * The amount can not be negative or superior to maximum MySQL DECIMAL for amount.
 * 
 * @author jerome
 *
 */

@AllArgsConstructor
@Getter
public class UserAmountException extends Exception {
	
	private static final long serialVersionUID = -7603232579220102794L;
	
	/**
	 * Error code.
	 */
	private final String errorCode;
	
	/**
	 * Message to be displayed in view.
	 */
	private final String defaultMessage;
	

}
