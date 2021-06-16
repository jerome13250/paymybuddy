package com.openclassrooms.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.service.interfaces.CalculationService;
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
	@MockBean
	private CalculationService calculationService;

	User user1;
	User user2;
	User user99;
	UserTransaction userTransaction1;
	UserTransaction userTransaction2;
	UserTransaction userTransaction3;
	Paged<UserTransaction> paged;

	@BeforeEach
	void setup() {
		user1 = new User(1L, "firstname1", "lastname1", "user1@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password1", true, "1AX256",
				new BigDecimal(100), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		user2 = new User(2L, "firstname2", "lastname2", "user2@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password21", true, "1AX256",
				new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		user99 = new User(99L, "firstname99", "lastname99", "anothertest@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),"password99", true, "1AX256",
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

		Paging paging = Paging.of(1, 1); //, 5);
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
	void PostUserTransaction_ShouldSucceedAndRedirected() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);
		//feesMap:
		BigDecimal transactionAmount = new BigDecimal("100");
		Currency transactionCurrency = Currency.getInstance("USD");
		Map<String, BigDecimal> feesMap = new HashMap<>();
		feesMap.put("finalAmount", new BigDecimal("99"));
		feesMap.put("fees", new BigDecimal("1"));
		when(calculationService.calculateFees(transactionAmount)).thenReturn(feesMap);
		//calculate amounts after transaction
		when(userServiceMock.sumAmountCalculate(user1,new BigDecimal("-100"),transactionCurrency)).thenReturn(new BigDecimal("0.00"));
		when(userServiceMock.sumAmountCalculate(user99,new BigDecimal("99"),transactionCurrency)).thenReturn(new BigDecimal("299.00"));

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "100")
				.param("currency", "USD")
				.param("userDestinationId","99")
				.with(csrf()))
		.andExpect(status().is3xxRedirection()) //redirection to usertransaction page
		.andExpect(redirectedUrl("/usertransaction"))
		;

		assertEquals(new BigDecimal("0.00"), user1.getAmount(), "expected result is 0.00");
		assertEquals(new BigDecimal("299.00"), user99.getAmount(), "expected result is 299.00");
		verify(userTransactionServiceMock,times(1)).create(any(UserTransaction.class),any(Map.class));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_ShouldFailAndReturnOk_AllFieldsNull() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.with(csrf()))
		.andExpect(status().isOk()) //Ok banktraction page to display error
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("usertransactionFormDTO", 3))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "userDestinationId", "NotNull"))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "amount", "NotNull"))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "currency", "NotNull"))
		;

		assertEquals(new BigDecimal("100"), user1.getAmount(), "initial value must not be modified");
		assertEquals(new BigDecimal("200"), user99.getAmount(), "initial value must not be modified");
		verify(userTransactionServiceMock,never()).create(any(UserTransaction.class),any(Map.class));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_ShouldFailAndReturnOk_userDestinationNotBuddy() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "1000")
				.param("currency", "USD")
				.param("userDestinationId","123") //Not a Buddy
				.with(csrf()))
		.andExpect(status().isOk()) //Ok banktraction page to display error
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("usertransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "userDestinationId", "userDestinationNotABuddy"))
		;

		assertEquals(new BigDecimal("100"), user1.getAmount(), "initial value must not be modified");
		assertEquals(new BigDecimal("200"), user99.getAmount(), "initial value must not be modified");
		verify(userTransactionServiceMock,never()).create(any(UserTransaction.class),any(Map.class));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_ShouldFailAndReturnOk_NotAllowedCurrency() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "1000")
				.param("currency", "CAD") //canadian dollar not allowed
				.param("userDestinationId","99") 
				.with(csrf()))
		.andExpect(status().isOk()) //Ok banktraction page to display error
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("usertransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "currency", "NotAllowedCurrency"))
		;

		assertEquals(new BigDecimal("100"), user1.getAmount(), "initial value must not be modified");
		assertEquals(new BigDecimal("200"), user99.getAmount(), "initial value must not be modified");
		verify(userTransactionServiceMock,never()).create(any(UserTransaction.class),any(Map.class));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_ShouldFailAndReturnOk_UserSourceAmountException() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);
		//feesMap:
		BigDecimal transactionAmount = new BigDecimal("1000");
		Currency transactionCurrency = Currency.getInstance("USD");
		Map<String, BigDecimal> feesMap = new HashMap<>();
		feesMap.put("finalAmount", new BigDecimal("990"));
		feesMap.put("fees", new BigDecimal("10"));
		when(calculationService.calculateFees(transactionAmount)).thenReturn(feesMap);
		//calculate amounts after transaction
		when(userServiceMock.sumAmountCalculate(user1,new BigDecimal("-1000"),transactionCurrency)).thenThrow(
				new UserAmountException("InsufficientFunds", "This amount exceeds your account value.")
				);
		//when(userServiceMock.sumAmountCalculate(user99,new BigDecimal("99"),transactionCurrency)).thenReturn(new BigDecimal("299.00"));

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "1000") //UserAmountException
				.param("currency", "USD") 
				.param("userDestinationId","99") 
				.with(csrf()))
		.andExpect(status().isOk()) //Ok banktraction page to display error
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("usertransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "amount", "InsufficientFunds"))
		;

		assertEquals(new BigDecimal("100"), user1.getAmount(), "initial value must not be modified");
		assertEquals(new BigDecimal("200"), user99.getAmount(), "initial value must not be modified");
		verify(userTransactionServiceMock,never()).create(any(UserTransaction.class),any(Map.class));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostUserTransaction_ShouldFailAndReturnOk_UserDestinationAmountException() throws Exception {
		//ARRANGE
		user1.getConnections().add(user99);
		when(userServiceMock.getCurrentUser()).thenReturn(user1);
		when(userTransactionServiceMock.getCurrentUserUserTransactionPage(1, 5)).thenReturn(paged); //display list of usertransactions
		when(userServiceMock.findById(99L)).thenReturn(user99);
		//feesMap:
		BigDecimal transactionAmount = new BigDecimal("100");
		Currency transactionCurrency = Currency.getInstance("USD");
		Map<String, BigDecimal> feesMap = new HashMap<>();
		feesMap.put("finalAmount", new BigDecimal("99"));
		feesMap.put("fees", new BigDecimal("1"));
		when(calculationService.calculateFees(transactionAmount)).thenReturn(feesMap);
		//calculate amounts after transaction
		when(userServiceMock.sumAmountCalculate(user1,new BigDecimal("-1000"),transactionCurrency)).thenReturn(new BigDecimal("0.00"));
		when(userServiceMock.sumAmountCalculate(user99,new BigDecimal("99"),transactionCurrency)).thenThrow(
				new UserAmountException("UserAmountExceedsMax", "Destination account can not exceed max value.")
				);

		//ACT+ASSERT:
		mockMvc.perform(post("/usertransaction")
				.param("amount", "100") //UserAmountException
				.param("currency", "USD") 
				.param("userDestinationId","99") 
				.with(csrf()))
		.andExpect(status().isOk()) //Ok banktraction page to display error
		.andExpect(view().name("usertransaction"))
		.andExpect(model().size(3))
		.andExpect(model().attributeErrorCount("usertransactionFormDTO", 1))
		.andExpect(model().attributeHasFieldErrorCode("usertransactionFormDTO", "amount", "UserAmountExceedsMax"))
		;

		assertEquals(new BigDecimal("100"), user1.getAmount(), "initial value must not be modified");
		assertEquals(new BigDecimal("200"), user99.getAmount(), "initial value must not be modified");
		verify(userTransactionServiceMock,never()).create(any(UserTransaction.class),any(Map.class));
	}



}
