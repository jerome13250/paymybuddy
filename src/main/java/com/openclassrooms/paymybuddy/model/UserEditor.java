package com.openclassrooms.paymybuddy.model;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.openclassrooms.paymybuddy.controller.UserTransactionController;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

/**
 * This class only exists because the POST unit test of UserTransactionControllerTest deactivates the JPA 
 * "magical" mapping from th:field="*{userDestination}" in usertransaction.html (id of the destination_user) to 
 * UserTransaction.userDestination which is a User type.<br> 
 * I create this only to be able to return a User object, since it is for test only i return always the same fake User...<br>
 * <b>
 * Otherwise the test returns : Cannot convert value of type 'java.lang.String' to required type 'com.openclassrooms.paymybuddy.model.User'
 *  for property 'userDestination': no matching editors or conversion strategy found 
 * </b>
 * @author jerome
 *
 */

//FIXME: this is an ugly solution to unit test error. Needs to be corrected...
public class UserEditor extends PropertyEditorSupport {

	Logger logger = LoggerFactory.getLogger(UserEditor.class);

	@Override
	public String getAsText() {
		User user = (User) getValue();
		String returnValue = user == null ? "" : user.getId().toString();
		logger.debug("PROPERTYEDITOR called : UserEditor.getAsText = {}",returnValue);
		
		return user == null ? "" : user.getId().toString();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		logger.debug("PROPERTYEDITOR called : UserEditor.setAsText");
		
		if (StringUtils.isEmpty(text)) {
			setValue(null);
		} else {
			//since it is for test only i return always the same fake User:
			User user54 = new User(54L, "firstname54", "lastname54", "anothertest@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password54", true, "1AX256",
					new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
			setValue(user54);
		}
	}
}