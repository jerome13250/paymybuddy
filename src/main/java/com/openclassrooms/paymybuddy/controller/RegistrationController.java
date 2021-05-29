package com.openclassrooms.paymybuddy.controller;

import java.util.Currency;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.openclassrooms.paymybuddy.config.CurrenciesAllowed;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.SecurityService;
import com.openclassrooms.paymybuddy.service.UserService;

@Controller
public class RegistrationController {

	Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
    private UserService userService;
	@Autowired
    private SecurityService securityService;
	
    @GetMapping("/registration")
    public String registration(Model model) { 
    	logger.info("GET: /registration");
    	model.addAttribute("userForm", new User());
        
        return "registration";
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
    	logger.info("POST: /registration");
    	
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        
        if ( userService.existsByEmail(userForm.getEmail()) ) {
        	bindingResult.rejectValue("email", "", "This email already exists");
            return "registration";
        }
        
        //need to save password for autologin, because userForm password will be encoded by userService
        String password = userForm.getPassword(); 
        userService.create(userForm);
       
        securityService.autoLogin(userForm.getEmail(), password);

        return "redirect:/";
    }
	
}
