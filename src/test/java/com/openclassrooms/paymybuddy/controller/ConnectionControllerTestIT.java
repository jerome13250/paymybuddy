package com.openclassrooms.paymybuddy.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConnectionControllerTestIT {

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void GETconnectionPageShouldReturnOK() throws Exception {
		mvc.perform(get("/connection")).andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void POSTconnectionPageShouldReturnOK() throws Exception {
		mvc.perform(post("/connection")
				.param("email", "bradpitt@mail.com")
				.with(csrf())
				).andDo(print()).andExpect(status().isOk());
	}

		

}
