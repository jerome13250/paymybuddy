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
    	model.addAttribute("user", userService.getConnectedUser());
    	return "connection";
    }
    
    @PostMapping("/connection")
    public String connectionAdd(@RequestParam String email , Model model) { 
    	logger.info("Calling: POST /connection");
    	User user = userService.getConnectedUser();
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
    	User user = userService.getConnectedUser();
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