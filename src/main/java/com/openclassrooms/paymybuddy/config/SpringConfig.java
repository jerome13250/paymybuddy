package com.openclassrooms.paymybuddy.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}


}