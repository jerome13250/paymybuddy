package com.openclassrooms.paymybuddy.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Check in user form that password and passwordconfirm are equals.
 * @author jerome
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordEqualityValidator.class)
public  @interface PasswordEquality {

	String message() default "Password confirm is different from password";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
