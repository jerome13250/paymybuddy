package com.openclassrooms.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.SecurityService;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;

/**
 * Unit test for UserController
 * @author jerome
 *
 */ 

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = UserController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest :
@Import(SpringWebTestConfig.class)

class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	@MockBean
	private SecurityService securityServiceMock;

	@Test
	void givenAnonymous_shouldBeRedirectedToLogin() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isFound()); //TODO: WHY 302 ????
	}

	@WithMockUser //annotation to test spring security with mock user : here we have default values "user"/"password"/"USER_ROLE"
	@Test
	void givenMockUser_shouldSucceedWith200() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void givenWithUserDetails_shouldSucceedWith200() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	void GetRegistrationForm_shouldSucceed() throws Exception {
		mockMvc.perform(get("/registration"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("registration"))
		.andExpect(model().size(1))
		.andExpect(model().attributeExists("userForm"));
	}

	@Test
	void PostRegistrationForm_shouldSucceedAndRedirected() throws Exception {
		mockMvc.perform(post("/registration")
				.param("firstname", "john")
				.param("lastname", "doe")
				.param("email", "johndoe@mail.com")
				.param("password", "123")
				.param("passwordconfirm", "123")
				.param("bankaccountnumber", "1AX123456789")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
	}

	@Test
	void PostRegistrationForm_shouldFailcause6FieldsEmpty() throws Exception {
		mockMvc.perform(post("/registration")
				.param("firstname", "")
				.param("lastname", "")
				.param("email", "")
				.param("password", "")
				.param("passwordconfirm", "")
				.param("bankaccountnumber", "")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 6))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "firstname", "NotBlank")) 
		.andExpect(status().isOk()); //registration page reloaded		
	}

	@Test
	void PostRegistrationForm_shouldFailcauseDifferentPasswords() throws Exception {
		mockMvc.perform(post("/registration")
				.param("firstname", "john")
				.param("lastname", "doe")
				.param("email", "johndoe@mail.com")
				.param("password", "123")
				.param("passwordconfirm", "123456789")
				.param("bankaccountnumber", "1AX123456789")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 1)) //error to display in registration page
		.andExpect(status().isOk()); //registration page reloaded		
	}

	@Test
	void PostRegistrationForm_shouldFailcausePasswordAlreadyExist() throws Exception {
		//ARRANGE
		when(userServiceMock.existsByEmail("johndoe@mail.com")).thenReturn(Boolean.TRUE);

		//ACT+ASSERT
		mockMvc.perform(post("/registration")
				.param("firstname", "john")
				.param("lastname", "doe")
				.param("email", "johndoe@mail.com")
				.param("password", "123")
				.param("passwordconfirm", "123")
				.param("bankaccountnumber", "1AX123456789")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 1)) //error to display in registration page
		.andExpect(status().isOk()); //registration page reloaded		
	}


	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetConnectionPage_shouldSucceed() throws Exception {
		//
		when(securityServiceMock.getCurrentUserDetailsUserName()).thenReturn("mockedEmail");
		when(userServiceMock.findByEmail("mockedEmail")).thenReturn(new User());

		mockMvc.perform(get("/connection"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(1))
		.andExpect(model().attributeExists("user"));
	}

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldSucceed() throws Exception {
		//
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User newConnection = new User(2L, "michael", "stone", "michaelstone@mail.com", LocalDateTime.of(2030, 01, 25, 00, 45),
				"password2", "", true, "12HGJ44", new BigDecimal(500), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );		
		
		when(securityServiceMock.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userServiceMock.findByEmail("johndoe@mail.com")).thenReturn(user);
		when(userServiceMock.existsByEmail("michaelstone@mail.com")).thenReturn(true);
		when(userServiceMock.findByEmail("michaelstone@mail.com")).thenReturn(newConnection);

		mockMvc.perform(post("/connection")
				.param("email", "michaelstone@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(1))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("user", user));
		
		assertTrue(user.getConnections().contains(newConnection));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldFail_connectionUnknown() throws Exception {
		//
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User newConnection = new User(2L, "michael", "stone", "michaelstone@mail.com", LocalDateTime.of(2030, 01, 25, 00, 45),
				"password2", "", true, "12HGJ44", new BigDecimal(500), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );		
		
		when(securityServiceMock.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userServiceMock.findByEmail("johndoe@mail.com")).thenReturn(user);
		when(userServiceMock.existsByEmail("michaelstone@mail.com")).thenReturn(false);
		//when(userServiceMock.findByEmail("michaelstone@mail.com")).thenReturn(newConnection);

		mockMvc.perform(post("/connection")
				.param("email", "michaelstone@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("user", user))
		.andExpect(model().attributeExists("error"))
		.andExpect(model().attribute("error", "Email Unknown"))
		;
		
		assertFalse(user.getConnections().contains(newConnection));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldFail_connectionToHimself() throws Exception {
		//
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		
		when(securityServiceMock.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userServiceMock.findByEmail("johndoe@mail.com")).thenReturn(user);
		when(userServiceMock.existsByEmail("johndoe@mail.com")).thenReturn(true);

		mockMvc.perform(post("/connection")
				.param("email", "johndoe@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("user", user))
		.andExpect(model().attributeExists("error"))
		.andExpect(model().attribute("error", "You can't add yourself as a connection"))
		;
		
		assertFalse(user.getConnections().contains(user));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionDeletePage_shouldSucceed() throws Exception {
		//
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User connection1 = new User(2L, "michael", "stone", "michaelstone@mail.com", LocalDateTime.of(2030, 01, 25, 00, 45),
				"password2", "", true, "12HGJ44", new BigDecimal(500), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );	
		User connection2 = new User(3L, "jason", "hill", "jasonhill@mail.com", LocalDateTime.of(2034, 01, 25, 00, 45),
				"password3", "", true, "12HGJ88", new BigDecimal(800), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );	
		user.getConnections().add(connection1);
		user.getConnections().add(connection2);
		
		
		when(securityServiceMock.getCurrentUserDetailsUserName()).thenReturn("johndoe@mail.com");
		when(userServiceMock.findByEmail("johndoe@mail.com")).thenReturn(user);
		when(userServiceMock.existsByEmail("jasonhill@mail.com")).thenReturn(true);

		mockMvc.perform(post("/connectionDelete")
				.param("id", "2")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/connection"))
		.andExpect(model().size(0))
		;
		
		assertFalse(user.getConnections().contains(connection1));
		assertTrue(user.getConnections().contains(connection2));
	}


}
