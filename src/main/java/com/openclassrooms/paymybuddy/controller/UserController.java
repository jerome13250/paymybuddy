package com.openclassrooms.paymybuddy.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@GetMapping("/")
	public String viewHomePage() {
		logger.info("Inside @GetMapping(\"/\")");
		return "index";
	}
    
	@GetMapping("/login")
	public String viewLoginPage() {
		logger.info("Inside @GetMapping(\"/login\")");
		return "login";
	}
	
    @GetMapping("/registration")
    public String registration(Model model) { //
        model.addAttribute("userForm", new User());
        return "registration";
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if ( emailExistInDatabase(userForm.getEmail()) ) {
        	bindingResult.rejectValue("email", "", "This email already exists");
            return "registration";
        }
        
        userService.create(userForm);
        //Note that we need to use the user passwordconfirm, password attribute is modified to crypted version in save()
        securityService.autoLogin(userForm.getEmail(), userForm.getPasswordconfirm());

        return "redirect:/";
    }
	
    private boolean emailExistInDatabase(String email){
    	return userService.findByEmail(email)!=null;
    }
    
    @GetMapping("/connection")
    public String connection(Model model) {
    	logger.info("Calling: GET /connection");
    	model.addAttribute("user", userService.findByEmail(securityService.getCurrentUserDetailsUserName()));
        return "connection";
    }
    
    @GetMapping("/connectionDelete")
    public String connectionDelete(@RequestParam Long id) { 
    	logger.info("Calling: GET /connectionDelete");
    	User user = userService.findByEmail(securityService.getCurrentUserDetailsUserName());
    	user.getConnections().removeIf(connectionUser -> (connectionUser.getId()==id) );
    	userService.update(user);
        
        return "redirect:/connection";
    }
    
    @GetMapping("/connectionAdd")
    public String connectionAdd(@RequestParam String email /*, BindingResult bindingResult*/) { 
    	logger.info("Calling: GET /connectionAdd");

    	/*
    	if ( !emailExistInDatabase(email) ) {
        	bindingResult.rejectValue("email", "", "This email is unknown");
            return "connection";
        }
    	*/
    	User user = userService.findByEmail(securityService.getCurrentUserDetailsUserName());
/*
    	if ( user.getEmail().equalsIgnoreCase(email) ) {
        	bindingResult.rejectValue("email", "", "You can't add yourself as a connection");
            return "connection";
        }
*/    	
        User newConnection = userService.findByEmail(email);
    	user.getConnections().add(newConnection);
    	userService.update(user);
        
        return "connection";
    }
    
    
    @GetMapping("/principal")
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }

}