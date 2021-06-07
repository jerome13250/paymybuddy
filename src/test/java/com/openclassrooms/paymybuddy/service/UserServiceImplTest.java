package com.openclassrooms.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.RoleRepository;
import com.openclassrooms.paymybuddy.repositories.UserRepository;
import com.openclassrooms.paymybuddy.service.impl.CalculationCurrencyService;
import com.openclassrooms.paymybuddy.service.impl.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.impl.UserServiceImpl;
import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
	LocalDateTime now;
	User user;
	
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
	CalculationCurrencyService calculationCurrencyService;
	@Mock
	LocalDateTimeService localDateTimeService;
	
	
	@BeforeEach
	void initialize() {
		
	}
	
	@Test
	void testCreateUser() {
		// Arrange
		LocalDateTime now = localDateTimeService.now();
		User user = new User(null,"John","Doe","johndoe@mail.com",now,"password","password",true,"1234",
				null,Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		User userExpected = new User(null,"John","Doe","johndoe@mail.com",now,"passwordencrypted","passwordencrypted",true,"1234",
				new BigDecimal("0"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		HashSet<Role> hashsetrole = new HashSet<>();
		hashsetrole.add(new Role(1L,"USER"));
		userExpected.setRoles(hashsetrole);
		
		when(bCryptPasswordEncoder.encode("password")).thenReturn("passwordencrypted");
		when(localDateTimeService.now()).thenReturn(now);
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
		assertEquals(userExpected.getPasswordconfirm(),user.getPasswordconfirm());
		assertEquals(userExpected.getRoles(),user.getRoles());
		assertEquals(userExpected.getUsertransactions(),user.getUsertransactions());

	}
	
	@Test
	void testUpdateUser() {
		// Arrange
		LocalDateTime now = localDateTimeService.now();
		User user = new User(1L,"John","Doe","johndoe@mail.com",now,"passwordencrypted","",true,"1234",
				new BigDecimal("1000"),Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
		User userExpected = new User(1L,"John","Doe","johndoe@mail.com",now,"passwordencrypted","passwordencrypted",true,"1234",
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
		assertEquals(userExpected.getPasswordconfirm(),user.getPasswordconfirm());
		assertEquals(userExpected.getRoles(),user.getRoles());
		assertEquals(userExpected.getUsertransactions(),user.getUsertransactions());
	}
	
	
	
	
}
