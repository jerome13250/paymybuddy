package com.openclassrooms.paymybuddy.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.service.interfaces.UserTransactionService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

/**
 * Unit test for UserTransactionController
 * @author jerome
 *
 */ 

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = UserTransactionController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest
//Need to create a CurrenciesAllowed bean because it is called directly in thymeleaf page : ${@currenciesAllowed.getCurrenciesAllowedList()}"
@Import( SpringWebTestConfig.class)


class UserTransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	@MockBean
	private UserTransactionService userTransactionServiceMock;
	
	User user1;
	UserTransaction userTransaction1;
	UserTransaction userTransaction2;
	UserTransaction userTransaction3;
	Paged<UserTransaction> paged;
	
	@BeforeEach
	void setup() {
		user1 = new User(1L, "firstname1", "lastname1", "user1e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password1", "", true, "1AX256",
				new BigDecimal(100), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User user2 = new User(2L, "firstname2", "lastname2", "user2e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password21", "", true, "1AX256",
				new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		userTransaction1 = new UserTransaction(1L, user1, user2, LocalDateTime.of(2025, 01, 01, 00, 45),
				new BigDecimal("100.10"),Currency.getInstance("EUR"), new BigDecimal("1.00"));
		userTransaction2 = new UserTransaction(2L, user1, user2, LocalDateTime.of(2025, 01, 01, 00, 45),
				new BigDecimal("200.20"),Currency.getInstance("USD"), new BigDecimal("2.00"));
		userTransaction3 = new UserTransaction(3L, user1, user2, LocalDateTime.of(2025, 01, 01, 00, 45), 
				new BigDecimal("300.30"),Currency.getInstance("GBP"), new BigDecimal("3.00"));
				
		UserTransaction[] userTransactionArray = {userTransaction1,userTransaction2,userTransaction3};
		List<UserTransaction> userTransactions = Arrays.asList(userTransactionArray);
		Page<UserTransaction> pagedUserTransaction = new PageImpl<UserTransaction>(userTransactions);
		
		Paging paging = Paging.of(1, 1, 5);
		paged = new Paged<UserTransaction>(pagedUserTransaction, paging);
	
	}
	
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetUserTransaction_shouldSucceed() throws Exception {
		//ARRANGE
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		
		//ACT+ASSERT
		mockMvc.perform(get("/usertransaction"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attributeExists("usertransactionFormDTO"))
		.andExpect(model().attributeExists("paged"))
		;
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_SendMoneyShouldSucceedAndRedirected() throws Exception {
		//ARRANGE
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		
		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "1000")
				.param("currency", "USD")
				.param("userDestination","53") //THIS CREATES BUG !
				.with(csrf()))
		.andExpect(status().is3xxRedirection()) //go to usertransaction page
		.andExpect(redirectedUrl("/usertransaction"))
		;
	}
	

}
