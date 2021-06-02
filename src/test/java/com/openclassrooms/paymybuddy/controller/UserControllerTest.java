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



}
