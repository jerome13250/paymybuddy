package com.openclassrooms.paymybuddy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.openclassrooms.paymybuddy.model.dto.UserFormDTO;

public class PasswordEqualityValidator implements ConstraintValidator<PasswordEquality, UserFormDTO>{

	@Override
	public boolean isValid(UserFormDTO userFormDTO, ConstraintValidatorContext context) {
		
		if ( userFormDTO.getPassword() == null || userFormDTO.getPasswordconfirm() == null ) {
			return false;
		}
		return userFormDTO.getPassword().equals(userFormDTO.getPasswordconfirm());
	}
}
