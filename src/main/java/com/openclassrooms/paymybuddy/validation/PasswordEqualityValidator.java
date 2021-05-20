package com.openclassrooms.paymybuddy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.openclassrooms.paymybuddy.model.User;

public class PasswordEqualityValidator implements ConstraintValidator<PasswordEquality, User>{

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		
		if ( user.getPassword() == null || user.getPasswordconfirm() == null ) {
			return false;
		}
		return user.getPassword().equals(user.getPasswordconfirm());
	}
}
