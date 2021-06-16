package com.openclassrooms.paymybuddy.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that loads a modelMapper bean. It is needed for DTO/Entity conversion.
 * 
 * @author jerome
 *
 */
@Configuration
public class SpringConfig {
    
	//This is for mapping Entity-DTO
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}

	
}