package com.openclassrooms.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@Controller
public class ConnectionController {

	Logger logger = LoggerFactory.getLogger(ConnectionController.class);

	@Autowired
    private UserService userService;

    @GetMapping("/connection")
    public String connectionTest(
    		@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size, 
            Model model) {
        model.addAttribute("paged", userService.getCurrentUserConnectionPage(pageNumber, size));
        return "connection";
    }

    @PostMapping("/connection")
    public String connectionAdd(@RequestParam String email , Model model) { 
    	logger.info("POST /connection");
    	User user = userService.getCurrentUser();
    	//check connection mail exists in DB:
    	if ( !userService.existsByEmail(email) ) {
    		model.addAttribute("error", "Email Unknown");
    		model.addAttribute("paged", userService.getCurrentUserConnectionPage(1, 5));
            return "connection";
        }
    	//Check connection to himself:
    	if ( user.getEmail().equalsIgnoreCase(email) ) {
    		model.addAttribute("error", "You can't add yourself as a connection");
    		model.addAttribute("paged", userService.getCurrentUserConnectionPage(1, 5));
            return "connection";
        }
    	
        User newConnection = userService.findByEmail(email);
    	user.getConnections().add(newConnection);
    	userService.update(user);

    	model.addAttribute("paged", userService.getCurrentUserConnectionPage(1, 5));
    	return "connection";
    }

    @PostMapping("/connectionDelete")
    public String connectionDelete(@RequestParam Long id) { 
    	logger.info("POST /connectionDelete");
    	User user = userService.getCurrentUser();
    	user.getConnections().removeIf(connectionUser -> (connectionUser.getId().equals(id)) );
    	userService.update(user);
        
        return "redirect:/connection";
    }
	
	
}
