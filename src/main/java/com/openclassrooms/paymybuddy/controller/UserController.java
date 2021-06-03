package com.openclassrooms.paymybuddy.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@Controller
public class UserController {
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserService userService;

	@Autowired
    private SecurityService securityService;

	@GetMapping("/")
	public String viewHomePage() {
		logger.info("GET: /");
		return "index";
	}
    
	@GetMapping("/login")
	public String viewLoginPage() {
		logger.info("GET: /login");
		return "login";
	}
	

    
    @GetMapping("/principal")
    @ResponseBody
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }

}