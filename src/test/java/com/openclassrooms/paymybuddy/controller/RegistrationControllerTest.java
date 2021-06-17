package com.openclassrooms.paymybuddy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = RegistrationController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest :
@Import(SpringWebTestConfig.class)
class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	@MockBean
	private SecurityService securityServiceMock;
	@MockBean
	private ModelMapper modelMapperMock;
	
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
				.param("currency", "USD")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
	}

	@Test
	void PostRegistrationForm_shouldFailcause6FieldsEmptyAndCurrencytypeMismatch() throws Exception {
		mockMvc.perform(post("/registration")
				.param("firstname", "")
				.param("lastname", "")
				.param("email", "")
				.param("password", "")
				.param("passwordconfirm", "")
				.param("bankaccountnumber", "")
				.param("currency", "")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 7))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "firstname", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "lastname", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "email", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "password", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "passwordconfirm", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "bankaccountnumber", "NotBlank"))
		.andExpect(model().attributeHasFieldErrorCode("userForm", "currency", "typeMismatch"))
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
				.param("currency", "USD")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 1)) //error to display in registration page
		.andExpect(status().isOk()); //registration page reloaded		
	}

	@Test
	void PostRegistrationForm_shouldFailcauseUserAlreadyExist() throws Exception {
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
				.param("currency", "USD")
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 1)) //error to display in registration page
		.andExpect(status().isOk()); //registration page reloaded		
	}

	@Test
	void PostRegistrationForm_shouldFailcauseCurrencyNotAllowed() throws Exception {
		//ARRANGE
		when(userServiceMock.existsByEmail("johndoe@mail.com")).thenReturn(Boolean.FALSE);

		//ACT+ASSERT
		mockMvc.perform(post("/registration")
				.param("firstname", "john")
				.param("lastname", "doe")
				.param("email", "johndoe@mail.com")
				.param("password", "123")
				.param("passwordconfirm", "123")
				.param("bankaccountnumber", "1AX123456789")
				.param("currency", "CAD") //canadian dollar not allowed
				.with(csrf()))
		.andExpect(model().attributeErrorCount("userForm", 1)) //error to display in registration page
		.andExpect(status().isOk()); //registration page reloaded		
	}

}
