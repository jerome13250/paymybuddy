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
import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankTransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

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
	
	BankTransaction bankTransaction1;
	BankTransaction bankTransaction2;
	BankTransaction bankTransaction3;
	Paged<BankTransaction> paged;
	
	@BeforeEach
	void setup() {
		User user1 = new User(1L, "firstname1", "lastname1", "user1e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(100), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		bankTransaction1 = new BankTransaction(1L, user1, "12345", LocalDateTime.of(2025, 01, 01, 00, 45), new BigDecimal("100.10"),Currency.getInstance("EUR"));
		bankTransaction2 = new BankTransaction(2L, user1, "12345", LocalDateTime.of(2025, 01, 01, 00, 45), new BigDecimal("200.20"),Currency.getInstance("USD"));
		bankTransaction3 = new BankTransaction(3L, user1, "12345", LocalDateTime.of(2025, 01, 01, 00, 45), new BigDecimal("300.30"),Currency.getInstance("GBP"));
				
		BankTransaction[] bankTransactionArray = {bankTransaction1,bankTransaction2,bankTransaction3};
		List<BankTransaction> bankTransactions = Arrays.asList(bankTransactionArray);
		Page<BankTransaction> pagedBankTransaction = new PageImpl<BankTransaction>(bankTransactions);
		
		Paging paging = Paging.of(1, 1, 5);
		paged = new Paged<BankTransaction>(pagedBankTransaction, paging);
	
	}
	
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetBankTransaction_shouldSucceed() throws Exception {
		//ARRANGE
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
		//ACT+ASSERT
		mockMvc.perform(get("/banktransaction"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("banktransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attributeExists("banktransactionFormDTO"))
		.andExpect(model().attributeExists("paged"))
		;
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_SendMoneyShouldSucceedAndRedirected() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		doThrow(new UserAmountException("InsufficientFunds", "This amount exceeds your account value."))
			.when(userServiceMock).updateAmount(any(User.class),any(BigDecimal.class),any(Currency.class));
		
		mockMvc.perform(post("/banktransaction")
				.param("amount", "1500")
				.param("currency", "USD")
				.param("getOrSendRadioOptions","send")
				.with(csrf()))
		.andExpect(status().isOk()) //return to banktraction page to display error
		.andExpect(view().name("banktransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("banktransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("banktransactionFormDTO", "amount", "InsufficientFunds"))
		;
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostBankTransaction_SendMoneyShouldFail_UnknownCurrency() throws Exception {
		
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(10000), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		
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
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(bankTransactionServiceMock.getCurrentUserBankTransactionPage(1, 5)).thenReturn(paged); //display list of banktransactions
		doThrow(new UserAmountException("UserAmountExceedsMax", "The user amount exceeds Max value."))
			.when(userServiceMock).updateAmount(any(User.class),any(BigDecimal.class),any(Currency.class));
		
		
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
