package com.openclassrooms.paymybuddy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;

/**
 * Unit test for LoginController
 * @author jerome
 *
 */ 

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = LoginController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest :
@Import(SpringWebTestConfig.class)

class LoginControllerTest {

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
