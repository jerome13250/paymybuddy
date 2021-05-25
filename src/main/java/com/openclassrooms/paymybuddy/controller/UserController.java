package com.openclassrooms.paymybuddy.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CurrencyService;
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
    private CurrencyService currencyService;
	
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
	

    
    @GetMapping("/connection")
    public String connection(Model model) {
    	logger.info("Calling: GET /connection");
    	model.addAttribute("user", userService.findByEmail(securityService.getCurrentUserDetailsUserName()));
    	return "connection";
    }
    
    @PostMapping("/connection")
    public String connectionAdd(@RequestParam String email , Model model) { 
    	logger.info("Calling: POST /connection");
    	User user = userService.findByEmail(securityService.getCurrentUserDetailsUserName());
    	model.addAttribute("user", user);
    	
    	if ( !userService.existsByEmail(email) ) {
    		model.addAttribute("error", "Email Unknown");
            return "connection";
        }
    	
    	if ( user.getEmail().equalsIgnoreCase(email) ) {
    		model.addAttribute("error", "You can't add yourself as a connection");
            return "connection";
        }
    	
        User newConnection = userService.findByEmail(email);
    	user.getConnections().add(newConnection);
    	userService.update(user);

    	return "connection";
    }
    
    
    @PostMapping("/connectionDelete")
    public String connectionDelete(@RequestParam Long id) { 
    	logger.info("Calling: POST /connectionDelete");
    	User user = userService.findByEmail(securityService.getCurrentUserDetailsUserName());
    	user.getConnections().removeIf(connectionUser -> (connectionUser.getId()==id) );
    	userService.update(user);
        
        return "redirect:/connection";
    }
    
    @GetMapping("/principal")
    @ResponseBody
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }

}