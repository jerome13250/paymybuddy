package com.openclassrooms.paymybuddy.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;

/**
 * Service for LocalDateTime.now()
 * This class exists only for being able to mock the LocalDateTime.now() in unit testing : 
 * <a href="https://www.jerriepelser.com/blog/unit-testing-with-dates/"> unit-testing-with-dates </a>
 * 
 * @author jerome
 *
 */
@Service
public class LocalDateTimeServiceImpl implements LocalDateTimeService {
	
	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}
