package com.openclassrooms.paymybuddy.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration test for UserController with SpringSecurity
 * @author jerome
 *
 */

/*
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
*/
class UserControllerTestIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	void loginPageShouldReturnOK() throws Exception {
		mvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	void userLoginTest() throws Exception {
		mvc.perform(formLogin("/login").user("bradpitt@mail.com").password("123")).andExpect(authenticated());
	}

	@Test
	void userLoginFailed() throws Exception {
		mvc.perform(formLogin("/login").user("testuser@mail.com").password("wrongpassword")).andExpect(unauthenticated());
	}
	
	

}
