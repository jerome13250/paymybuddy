package com.openclassrooms.paymybuddy.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.SecurityService;
import com.openclassrooms.paymybuddy.service.UserService;

@Controller
public class UserController {
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;
	
	@GetMapping("/login")
	public String viewLoginPage() {
		logger.info("Inside @GetMapping(\"/login\")");
		return "login";
	}
	
	@GetMapping("/")
	public String viewHomePage() {
		logger.info("Inside @GetMapping(\"/\")");
		return "index";
	}
	
    @GetMapping("/registration")
    public String registration(Model model) { //

        model.addAttribute("userForm", new User());

        return "registration";
    }
    
    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        //TODO:
    	//userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        
        
        logger.debug("userForm : {}",userForm.toString());
 
        userService.save(userForm);
        securityService.autoLogin(userForm.getEmail(), userForm.getPasswordconfirm());

        return "redirect:/";
    }
	
    @GetMapping("/principal")
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }

}