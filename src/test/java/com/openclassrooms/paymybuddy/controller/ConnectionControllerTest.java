package com.openclassrooms.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.testconfig.SpringWebTestConfig;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

//@WebMvcTest tells Spring Boot to instantiate only the web layer and not the entire context
@WebMvcTest(controllers = ConnectionController.class) 
//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest :
@Import(SpringWebTestConfig.class)
class ConnectionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userServiceMock;
	
	User user1;
	User user2;
	User user3;
	Paged<User> paged;
	
	@BeforeEach
	void setup() {
		user1 = new User(1L, "firstname1", "lastname1", "user1e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(100), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		user2 = new User(2L, "firstname2", "lastname2", "user2e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password2", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		user3 = new User(3L, "firstname3", "lastname3", "user3e@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password3", "", true, "1AX256", new BigDecimal(300), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
	
		User[] userArray = {user1,user2,user3};
		List<User> users = Arrays.asList(userArray);
		Page<User> pagedUser = new PageImpl<User>(users);
		
		Paging paging = Paging.of(1, 1, 5);
		paged = new Paged<User>(pagedUser, paging);
	
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void GetConnectionPage_shouldSucceed() throws Exception {
		//ARRANGE:
		when(userServiceMock.getCurrentUserConnectionPage(1, 5)).thenReturn(paged); //display list of connections

		//ACT+ASSERT:
		mockMvc.perform(get("/connection"))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(1))
		.andExpect(model().attributeExists("paged"));
	}
	

	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldSucceed() throws Exception {
		//ARRANGE:
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User newConnection = new User(2L, "michael", "stone", "michaelstone@mail.com", LocalDateTime.of(2030, 01, 25, 00, 45),
				"password2", "", true, "12HGJ44", new BigDecimal(500), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );		
		
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(userServiceMock.existsByEmail("michaelstone@mail.com")).thenReturn(true);
		when(userServiceMock.findByEmail("michaelstone@mail.com")).thenReturn(newConnection);
		when(userServiceMock.getCurrentUserConnectionPage(1, 5)).thenReturn(paged); //display list of connections
		
		mockMvc.perform(post("/connection")
				.param("email", "michaelstone@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(1))
		.andExpect(model().attributeExists("paged"))
		.andExpect(model().attribute("paged", paged));
		
		assertTrue(user.getConnections().contains(newConnection));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldFail_connectionUnknown() throws Exception {
		//ARRANGE:
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		User newConnection = new User(2L, "michael", "stone", "michaelstone@mail.com", LocalDateTime.of(2030, 01, 25, 00, 45),
				"password2", "", true, "12HGJ44", new BigDecimal(500), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );		

		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(userServiceMock.existsByEmail("michaelstone@mail.com")).thenReturn(false);//check connection exists in DB
		when(userServiceMock.getCurrentUserConnectionPage(1, 5)).thenReturn(paged); //display list of connections
		
		//ACT+ASSERT:
		mockMvc.perform(post("/connection")
				.param("email", "michaelstone@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("paged"))
		.andExpect(model().attribute("paged", paged))
		.andExpect(model().attributeExists("error"))
		.andExpect(model().attribute("error", "Email Unknown"))
		;
		
		assertFalse(user.getConnections().contains(newConnection));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionPage_shouldFail_connectionToHimself() throws Exception {
		//ARRANGE:
		User user = new User(1L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		when(userServiceMock.existsByEmail("johndoe@mail.com")).thenReturn(true); //check connection to himself
		when(userServiceMock.getCurrentUserConnectionPage(1, 5)).thenReturn(paged); //display list of connections
		
		//ACT+ASSERT:
		mockMvc.perform(post("/connection")
				.param("email", "johndoe@mail.com")
				.with(csrf()))
		.andExpect(status().is2xxSuccessful())
		.andExpect(view().name("connection"))
		.andExpect(model().size(2))
		.andExpect(model().attributeExists("paged"))
		.andExpect(model().attribute("paged", paged))
		.andExpect(model().attributeExists("error"))
		.andExpect(model().attribute("error", "You can't add yourself as a connection"))
		;
		
		assertFalse(user.getConnections().contains(user));
	}
	
	@WithUserDetails("user@company.com") //user from SpringSecurityWebTestConfig.class
	@Test
	void PostConnectionDeletePage_shouldSucceed() throws Exception {
		//ARRANGE:
		User user = new User(1000L, "john", "doe", "johndoe@mail.com", LocalDateTime.of(2025, 01, 01, 00, 45),
				"password1", "", true, "1AX256", new BigDecimal(200), Currency.getInstance("USD"), new HashSet<>(), new HashSet<>(), new HashSet<>() );
		user.getConnections().add(user1);
		user.getConnections().add(user2);
		when(userServiceMock.getCurrentUser()).thenReturn(user);
		
		//ACT+ASSERT:
		mockMvc.perform(post("/connectionDelete")
				.param("id", "1")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/connection"))
		.andExpect(model().size(0))
		;
		
		assertFalse(user.getConnections().contains(user1));
		assertTrue(user.getConnections().contains(user2));
	}

	
}
