package com.openclassrooms.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for login
 * @author jerome
 *
 */

@Controller
public class LoginController {
	
	Logger logger = LoggerFactory.getLogger(LoginController.class);

  
	@GetMapping("/login")
	public String viewLoginPage() {
		logger.info("GET: /login");
		return "login";
	}
	
}