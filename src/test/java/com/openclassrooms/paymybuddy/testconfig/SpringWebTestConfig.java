package com.openclassrooms.paymybuddy.testconfig;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.openclassrooms.paymybuddy.config.CurrenciesAllowed;

//Configuration classes annotated with @TestConfiguration are excluded from component scanning, therefore we need to import it
//explicitly in every test where we want to @Autowire it. We can do that with the @Import annotation.
@TestConfiguration
public class SpringWebTestConfig {

	//Need to create a UserDetailsService in SpringSecurityWebTestConfig.class because @Service are not loaded by @WebMvcTest
	@Bean
	public UserDetailsService userDetailsService() {
		User basicUser = new User("user@company.com", "password", Arrays.asList(
				new SimpleGrantedAuthority("ROLE_USER")
				));
		return new InMemoryUserDetailsManager(Arrays.asList(
				basicUser /*, managerActiveUser*/
				));
	}
	
	//Need to create a CurrenciesAllowed bean because it is called directly in thymeleaf page : ${@currenciesAllowed.getCurrenciesAllowedList()}"
	@Bean
	public CurrenciesAllowed currenciesAllowed() {
		String[] listCurrencies = {"USD","EUR"};
		return new CurrenciesAllowed( listCurrencies );
	}
	
	//This is for mapping Entity-DTO
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
}