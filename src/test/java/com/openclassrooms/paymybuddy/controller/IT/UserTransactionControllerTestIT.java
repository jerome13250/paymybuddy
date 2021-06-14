package com.openclassrooms.paymybuddy.controller.IT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Currency;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
//We need to use @Transactional here because otherwise we get LazyInitializationException due to User.roles/banktransactions/connections are FetchType.LAZY
//Session does not exist in test context, @Transactional creates a session context, this way we can fetch lazily roles/banktransactions/connections
//https://stackoverflow.com/questions/19813492/getting-lazyinitializationexception-on-junit-test-case#answer-20985746
//Another solution would be to use FetchType.EAGER, but THIS HAS A NEGATIVE IMPACT:
//it can lead to performance issues in the real application, explanations here: https://vladmihalcea.com/eager-fetching-is-a-code-smell
//NOTE: @Transactional creates a new transaction that is by default automatically ROLLED BACK after test completion.
@Transactional
class UserTransactionControllerTestIT {
	
	Logger logger = LoggerFactory.getLogger(UserTransactionControllerTestIT.class);

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private UserService userservice;

	@BeforeEach
	void initializeDatabaseValues () {
		User userTest = userservice.findByEmail("test@mail.com");
		userTest.setAmount(new BigDecimal("1000"));
		userservice.update(userTest);
		
		User userAnotherTest = userservice.findByEmail("anothertest@mail.com");
		userAnotherTest.setAmount(new BigDecimal("1000"));
		userservice.update(userAnotherTest);
	}
	
	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void getUsertransactionShouldReturnOK() throws Exception {
		mvc.perform(get("/usertransaction")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username="test@mail.com") //test@mail.com exists in our test database
	void postUsertransaction() throws Exception {
		mvc.perform(post("/usertransaction")
				.param("userDestination", "54")
				.param("amount", "100")
				.param("currency", "USD")
				.with(csrf())
				)//.andDo(print())
		.andExpect(status().is3xxRedirection());
		
		User userTest = userservice.findByEmail("test@mail.com");
		User userAnotherTest = userservice.findByEmail("anothertest@mail.com");
		assertEquals(new BigDecimal("900.00"),userTest.getAmount(), "1000 - user transaction");
		assertEquals(new BigDecimal("1099.50"),userAnotherTest.getAmount(), "1000 + user transaction - fees (0.5%)");
		
		UserTransaction userTransaction = userTest.getUsertransactions().iterator().next();
		assertEquals(userTest, userTransaction.getUserSource());
		assertEquals(userAnotherTest, userTransaction.getUserDestination());
		//get time in second between transaction datetime and now :
		long durationInSec = Duration.between(userTransaction.getDatetime(),LocalDateTime.now()).getSeconds();
		assertTrue(durationInSec<2, "time difference between transaction time and now can't be more than 2s");
		assertEquals(new BigDecimal("99.50"),userTransaction.getAmount());
		assertEquals(Currency.getInstance("USD"),userTransaction.getCurrency());
		assertEquals(new BigDecimal("0.50"),userTransaction.getFees());
		
		
	}

}
