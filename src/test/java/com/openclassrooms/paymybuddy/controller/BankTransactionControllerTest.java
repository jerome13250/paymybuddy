package com.openclassrooms.paymybuddy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.model.Currency;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CurrencyService;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringSecurityWebTestConfig;

/**
 * Unit test for BankTransactionController
 * @author jerome
 *
 */ 

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = BankTransactionController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest :
@Import(SpringSecurityWebTestConfig.class)

class BankTransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	@MockBean
	private CurrencyService currencyServiceMock;

	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetBankTransaction_shouldSucceed() throws Exception {
		//ARRANGE
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), new Currency(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		Currency currency1 = new Currency(1L,"Euro","EUR","€");
		List<Currency> currencies = Arrays.asList(currency1);
		when(currencyServiceMock.findAll()).thenReturn(currencies);
		
		//ACT+ASSERT
		mockMvc.perform(get("/banktransaction"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("banktransaction"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attributeExists("currencies"))
		;
	}


}
