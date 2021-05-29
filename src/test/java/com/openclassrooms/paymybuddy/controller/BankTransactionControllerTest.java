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
import java.util.Currency;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankTransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;

/**
 * Unit test for BankTransactionController
 * @author jerome
 *
 */ 

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = BankTransactionController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest
//Need to create a CurrenciesAllowed bean because it is called directly in thymeleaf page : ${@currenciesAllowed.getCurrenciesAllowedList()}"
@Import( SpringWebTestConfig.class)


class BankTransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	@MockBean
	private BankTransactionService bankTransactionServiceMock;
	
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetBankTransaction_shouldSucceed() throws Exception {
		//ARRANGE
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);

		
		//ACT+ASSERT
		mockMvc.perform(get("/banktransaction"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("banktransaction"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attributeExists("banktransactionFormDTO"))
		;
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_SendMoneyShouldSucceedAndRedirected() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "USD")
				.param("getOrSendRadioOptions","send")
				.with(csrf()))
		.andExpect(status().is3xxRedirection()) //go to banktraction page and resets the model
		.andExpect(redirectedUrl("/banktransaction"))
		;
	}
		
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_SendMoneyShouldFail_InsufficientFund() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(50), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		doThrow(UserAmountException.class).when(userServiceMock).updateAmount(any(User.class),any(BigDecimal.class),any(Currency.class));
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "USD")
				.param("getOrSendRadioOptions","send")
				.with(csrf()))
		.andExpect(status().isOk()) //return to banktraction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "amount", "InsufficientFunds"))
		;
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_SendMoneyShouldFail_UnknownCurrency() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "BRL") //Brazilian Real
				.param("getOrSendRadioOptions","send")
				.with(csrf()))
		.andExpect(status().isOk()) //return to banktraction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "currency", "UnknownCurrency"))
		;
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_GetMoneyShouldSucceedAndRedirected() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "USD")
				.param("getOrSendRadioOptions","get")
				.with(csrf()))
		.andExpect(status().is3xxRedirection()) //go to banktraction page and resets the model
		.andExpect(redirectedUrl("/banktransaction"))
		;
	}

	@WithUserDetails("user@company.com")
	@Test
	void PostBankTransaction_GetMoneyShouldFail_MissingAmount() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				//.param("amount", "1500")
				.param("currency", "USD")
				.param("getOrSendRadioOptions","get")
				.with(csrf()))
		.andExpect(status().isOk()) //banktransaction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "amount", "NotNull"))
		;
	}
	
	@WithUserDetails("user@company.com")
	@Test
	void PostBankTransaction_GetMoneyShouldFail_MissingCurrency() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				//.param("currency", "USD")
				.param("getOrSendRadioOptions","get")
				.with(csrf()))
		.andExpect(status().isOk()) //banktransaction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "currency", "UnknownCurrency"))
		;
	}

	@WithUserDetails("user@company.com")
	@Test
	void PostBankTransaction_GetMoneyShouldFail_MissingParamGetOrSend() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "USD")
				//.param("getOrSendRadioOptions","get")
				.with(csrf()))
		.andExpect(status().isOk()) //banktransaction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "getOrSendRadioOptions", "NotBlank"))
		;
	}

	@WithUserDetails("user@company.com")
	@Test
	void PostBankTransaction_GetMoneyShouldFail_UserAmountExceedsMySQLField() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(5000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getConnectedUser()).thenReturn(user);
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "99999999") 
				.param("currency", "USD")
				.param("getOrSendRadioOptions","get")
				.with(csrf()))
		.andExpect(status().isOk()) //banktransaction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "amount", "UserAmountExceedsMax"))
		;
	}
	
	
	
}
