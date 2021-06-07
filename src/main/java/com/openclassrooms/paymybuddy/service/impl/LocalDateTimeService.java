package com.openclassrooms.paymybuddy.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 * This class exists only for being able to mock the LocalDateTime.now() in unit testing...
 * @author jerome
 *
 */
@Service
public class LocalDateTimeService {
	
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}
