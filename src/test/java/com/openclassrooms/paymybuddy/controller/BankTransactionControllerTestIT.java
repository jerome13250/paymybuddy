package com.openclassrooms.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BankTransactionControllerTestIT {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private UserService userService;
	
	@BeforeEach
	void initialize() {
		//user test@mail.com always has 1000 USD on his account
		User user = userService.findByEmail("test@mail.com");
		user.setAmount(new BigDecimal(5000));
		userService.update(user);
	}
	

	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void getBanktransactionShouldReturnOK() throws Exception {
		mvc.perform(get("/banktransaction")).andDo(print()).andExpect(status().isOk());
	}
	
	
	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void postBanktransactionGetMoney() throws Exception {
		mvc.perform(post("/banktransaction")
				.param("getOrSendRadioOptions", "get")
				.param("amount", "100")
				.param("currency", "USD")
				.with(csrf())
				).andDo(print()).andExpect(status().is3xxRedirection());
		
		//check user account value after transaction:
		User user = userService.findByEmail("test@mail.com");
		assertEquals(new BigDecimal("1100"),user.getAmount());
		
	}
	
	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void postBanktransactionSendMoney() throws Exception {
		mvc.perform(post("/banktransaction")
				.param("getOrSendRadioOptions", "send")
				.param("amount", "50")
				.param("currency", "USD")
				.with(csrf())
				).andDo(print()).andExpect(status().is3xxRedirection());
	}

		

}
