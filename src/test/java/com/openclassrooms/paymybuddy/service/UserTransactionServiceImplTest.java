package com.openclassrooms.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.repositories.UserTransactionRepository;
import com.openclassrooms.paymybuddy.service.impl.UserTransactionServiceImpl;
import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

@ExtendWith(MockitoExtension.class)
public class UserTransactionServiceImplTest {
	
	@InjectMocks
	UserTransactionServiceImpl userTransactionServiceImpl;
	
	@Mock
	UserTransactionRepository userTransactionRepository;
	@Mock
	UserService userService;
	@Mock
	LocalDateTimeService localDateTimeServiceImpl;
	@Mock
	PagingService pagingService;
	
	LocalDateTime now;
	User user1;
	User user2;
	UserTransaction userTransaction1;

	@BeforeEach
	void initialize() {
		now = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);
		user1 = new User(1L,"John","Doe","johndoe@mail.com",now,"password",true,"1234",
				null,Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		user2 = new User(2L,"Jane","Doe","janedoe@mail.com",now,"password",true,"4321",
				null,Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		
		userTransaction1 = new UserTransaction(50L, user1, user2, now, new BigDecimal("90"), Currency.getInstance("USD"), new BigDecimal("10"));
	}
	
	@Test
	void testCreate() {
		//Arrange
		
		when(userService.getCurrentUser()).thenReturn(user1);
		when(localDateTimeServiceImpl.now()).thenReturn(now);

		UserTransaction userTransactionExpected = new UserTransaction(null, user1, user2, now, new BigDecimal("90"), Currency.getInstance("USD"), new BigDecimal("10"));
		
		UserTransaction userTransactionToCreate = new UserTransaction(); //initial data data from DTO
		userTransactionToCreate.setUserDestination(user2);
		userTransactionToCreate.setAmount(new BigDecimal("1000"));
		userTransactionToCreate.setCurrency(Currency.getInstance("USD"));;

		Map<String,BigDecimal> fees = new HashMap<>();
		fees.put("fees", new BigDecimal("10"));
		fees.put("finalAmount", new BigDecimal("90"));
		
		//Act
		userTransactionServiceImpl.create(userTransactionToCreate, fees);

		// Assert
		verify(userTransactionRepository, times(1)).save(userTransactionToCreate);
		assertNull(userTransactionToCreate.getId());
		
		assertEquals(userTransactionExpected.getAmount(),userTransactionToCreate.getAmount());
		assertEquals(userTransactionExpected.getCurrency(),userTransactionToCreate.getCurrency());
		assertEquals(userTransactionExpected.getDatetime(),userTransactionToCreate.getDatetime());
		assertEquals(userTransactionExpected.getFees(),userTransactionToCreate.getFees());
		assertEquals(userTransactionExpected.getId(),userTransactionToCreate.getId());
		assertEquals(userTransactionExpected.getUserDestination(),userTransactionToCreate.getUserDestination());
		assertEquals(userTransactionExpected.getUserSource(),userTransactionToCreate.getUserSource());

	}
	
	
	@Test
	void test_getCurrentUserUserTransactionPage() throws Exception {
		// Arrange
		
		//Page:
		when(userService.getCurrentUser()).thenReturn(user1);
		List<UserTransaction> userTransactions = new ArrayList<>();
		userTransactions.add(userTransaction1);
		Page<UserTransaction> expectedPage = new PageImpl<>(userTransactions);
		when(userTransactionRepository.findUserTransactionByUserId(any(Long.class),any(Pageable.class))).thenReturn(expectedPage);
		//Paging:
		Paging expectedPaging = new Paging(false, false, 1, new ArrayList<>());
		when(pagingService.of(any(Integer.class), any(Integer.class))).thenReturn(expectedPaging);
		
		// Act
		Paged<UserTransaction> pagedUserTransaction =  userTransactionServiceImpl.getCurrentUserUserTransactionPage(1, 5);

		// Assert
		assertEquals(expectedPage,pagedUserTransaction.getPage());
		assertEquals(expectedPaging,pagedUserTransaction.getPaging());

	}
	

}
