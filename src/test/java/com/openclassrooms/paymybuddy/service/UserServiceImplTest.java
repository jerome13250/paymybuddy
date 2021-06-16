package com.openclassrooms.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.RoleRepository;
import com.openclassrooms.paymybuddy.repositories.UserRepository;
import com.openclassrooms.paymybuddy.service.impl.UserServiceImpl;
import com.openclassrooms.paymybuddy.service.interfaces.CalculationService;
import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userServiceImpl;

	@Mock
	UserRepository userRepository;
	@Mock
	RoleRepository roleRepository;
	@Mock
	SecurityService securityService;
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock
	CalculationService calculationService;
	@Mock
	LocalDateTimeService localDateTimeServiceImpl;
	@Mock
	PagingService pagingService;

	LocalDateTime now;
	User user1;
	User user2;

	@BeforeEach
	void initialize() {
		user1 = new User(1L,"John","Doe","johndoe@mail.com",now,"password",true,"1234",
				new BigDecimal("100"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		user2 = new User(2L,"Jane","Doe","janedoe@mail.com",now,"password",true,"1234",
				new BigDecimal("100"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
	}

	@Test
	void testCreateUser() {
		// Arrange
		LocalDateTime now = localDateTimeServiceImpl.now();
		User user = new User(null,"John","Doe","johndoe@mail.com",now,"password",true,"1234",
				null,Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		User userExpected = new User(null,"John","Doe","johndoe@mail.com",now,"passwordencrypted",true,"1234",
				new BigDecimal("0"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		HashSet<Role> hashsetrole = new HashSet<>();
		hashsetrole.add(new Role(1L,"USER"));
		userExpected.setRoles(hashsetrole);

		when(bCryptPasswordEncoder.encode("password")).thenReturn("passwordencrypted");
		when(localDateTimeServiceImpl.now()).thenReturn(now);
		when(roleRepository.findByRolename("USER")).thenReturn(new Role(1L,"USER"));

		// Act
		userServiceImpl.create(user);

		// Assert
		verify(userRepository, times(1)).save(user);
		//It's not possible to compute hashcode ( @EqualsAndHashcode ) on User object since Hibernate makes it a recursive object (due to List of users contained inside)
		//hence if we add @EqualsAndHashcode on User it makes the real application fail...
		//That's why we don't use .equals() here, so i do a list of assertEquals:
		assertNull(user.getId(),"Must be null for user creation");
		assertEquals(userExpected.getFirstname(),user.getFirstname());
		assertEquals(userExpected.getLastname(),user.getLastname());
		assertEquals(userExpected.getAmount(),user.getAmount());
		assertEquals(userExpected.getBankaccountnumber(),user.getBankaccountnumber());
		assertEquals(userExpected.getBanktransactions(),user.getBanktransactions());
		assertEquals(userExpected.getConnections(),user.getConnections());
		assertEquals(userExpected.getCurrency(),user.getCurrency());
		assertEquals(userExpected.getEmail(),user.getEmail());
		assertEquals(userExpected.getEnabled(),user.getEnabled());
		assertEquals(userExpected.getInscriptiondatetime(),user.getInscriptiondatetime());
		assertEquals(userExpected.getPassword(),user.getPassword());
		//assertEquals(userExpected.getPasswordconfirm(),user.getPasswordconfirm());
		assertEquals(userExpected.getRoles(),user.getRoles());
		assertEquals(userExpected.getUsertransactions(),user.getUsertransactions());

	}

	@Test
	void testUpdateUser() {
		// Arrange
		LocalDateTime now = localDateTimeServiceImpl.now();
		User user = new User(1L,"John","Doe","johndoe@mail.com",now,"passwordencrypted",true,"1234",
				new BigDecimal("1000"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		User userExpected = new User(1L,"John","Doe","johndoe@mail.com",now,"passwordencrypted",true,"1234",
				new BigDecimal("1000"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());

		// Act
		userServiceImpl.update(user);

		// Assert
		verify(userRepository, times(1)).save(user);
		assertEquals(userExpected.getId(),user.getId());
		assertEquals(userExpected.getFirstname(),user.getFirstname());
		assertEquals(userExpected.getLastname(),user.getLastname());
		assertEquals(userExpected.getAmount(),user.getAmount());
		assertEquals(userExpected.getBankaccountnumber(),user.getBankaccountnumber());
		assertEquals(userExpected.getBanktransactions(),user.getBanktransactions());
		assertEquals(userExpected.getConnections(),user.getConnections());
		assertEquals(userExpected.getCurrency(),user.getCurrency());
		assertEquals(userExpected.getEmail(),user.getEmail());
		assertEquals(userExpected.getEnabled(),user.getEnabled());
		assertEquals(userExpected.getInscriptiondatetime(),user.getInscriptiondatetime());
		assertEquals(userExpected.getPassword(),user.getPassword());
		//assertEquals(userExpected.getPasswordconfirm(),user.getPasswordconfirm());
		assertEquals(userExpected.getRoles(),user.getRoles());
		assertEquals(userExpected.getUsertransactions(),user.getUsertransactions());
	}

	@Test
	void testFindByEmail() {
		// Arrange
		String email = "johndoe@mail.com";
		User user = new User(null,"John","Doe","johndoe@mail.com",now,"password",true,"1234",
				null,Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		when(userRepository.findByEmail(email)).thenReturn(user);
		// Act
		User resultUser = userServiceImpl.findByEmail(email);
		// Assert
		assertEquals(resultUser, user);
	}


	@Test
	void testExistsByEmail() {
		// Arrange
		String email = "johndoe@mail.com";
		when(userRepository.existsByEmail(email)).thenReturn(true);
		// Act
		Boolean result = userServiceImpl.existsByEmail(email);
		// Assert
		assertTrue(result);
	}

	@Test
	void testGetCurrentUser() {
		// Arrange
		when(securityService.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userRepository.findByEmail("johndoe@mail.com")).thenReturn(user1);
		// Act
		User resultUser = userServiceImpl.getCurrentUser();
		// Assert
		assertEquals(resultUser, user1);
	}

	@Test
	void testUpdateAmount() throws Exception {
		// Arrange
		when(calculationService.sumCurrencies(new BigDecimal("100"), Currency.getInstance("USD"),
				new BigDecimal("100"), Currency.getInstance("USD"))).thenReturn(new BigDecimal("200"));

		// Act
		BigDecimal result = userServiceImpl.sumAmountCalculate(user1, new BigDecimal("100"), Currency.getInstance("USD"));

		// Assert
		assertEquals(result,new BigDecimal("200"));

	}


	@Test
	void testUpdateAmount_Exception_InsufficientFund() throws Exception {
		// Arrange
		when(calculationService.sumCurrencies(new BigDecimal("100"), Currency.getInstance("USD"),
				new BigDecimal("-500"), Currency.getInstance("USD"))).thenReturn(new BigDecimal("-400"));

		// Act+Assert
		assertThrows(UserAmountException.class, () -> {
			userServiceImpl.sumAmountCalculate(user1, new BigDecimal("-500"), Currency.getInstance("USD"));
		});
	}

	@Test
	void testUpdateAmount_Exception_UserAmountExceedsMax() throws Exception {
		// Arrange
		when(calculationService.sumCurrencies(new BigDecimal("100"), Currency.getInstance("USD"),
				new BigDecimal("1000000000000000"), Currency.getInstance("USD"))).thenReturn(new BigDecimal("1000000000000100"));

		// Act+Assert
		assertThrows(UserAmountException.class, () -> {
			userServiceImpl.sumAmountCalculate(user1, new BigDecimal("1000000000000000"), Currency.getInstance("USD"));
		});
	}

	@Test
	void test_getCurrentUserConnectionPage() throws Exception {
		// Arrange
		when(securityService.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userRepository.findByEmail("johndoe@mail.com")).thenReturn(user1);

		List<User> users = new ArrayList<>();
		users.add(user2);
		Page<User> expectedPage = new PageImpl<>(users); // https://stackoverflow.com/questions/55448188/spring-boot-pagination-mockito-repository-findallpageable-returns-null
		when(userRepository.findConnectionById(any(Long.class), any(Pageable.class))).thenReturn(expectedPage);
		Paging expectedPaging = new Paging(false, false, 1, new ArrayList<>());
		when(pagingService.of(any(Integer.class), any(Integer.class))).thenReturn(expectedPaging);

		// Act
		Paged<User> pagedUser =  userServiceImpl.getCurrentUserConnectionPage(1, 5);

		// Assert
		assertEquals(expectedPage,pagedUser.getPage());
		assertEquals(expectedPaging,pagedUser.getPaging());

	}

	@Test
	void test_findById() {
		//Arrange
		Optional<User> optUser = Optional.of(user1);
		when(userRepository.findById(1L)).thenReturn(optUser);

		//Act
		User resultUser = userServiceImpl.findById(1L);

		// Assert
		assertEquals(optUser.get(),resultUser);

	}

	
	@Test
	void test_findById_NotFound() {
		//Arrange
		Optional<User> optUser = Optional.empty();
		when(userRepository.findById(1L)).thenReturn(optUser);

		//Act
		User resultUser = userServiceImpl.findById(1L);

		// Assert
		assertNull(resultUser);

	}

}
