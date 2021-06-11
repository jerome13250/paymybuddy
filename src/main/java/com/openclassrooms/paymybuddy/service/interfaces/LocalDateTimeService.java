package com.openclassrooms.paymybuddy.service.interfaces;

import java.time.LocalDateTime;

/**
 * This interface exists only for being able to mock the LocalDateTime.now() in unit testing...
 * @author jerome
 *
 */
public interface LocalDateTimeService {

	LocalDateTime now();

}