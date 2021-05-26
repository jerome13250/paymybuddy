package com.openclassrooms.paymybuddy.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CurrencyService;
import com.openclassrooms.paymybuddy.service.SecurityService;
import com.openclassrooms.paymybuddy.service.UserService;

@Controller
public class BankTransactionController {

	Logger logger = LoggerFactory.getLogger(BankTransactionController.class);

	@Autowired
    private UserService userService;
	@Autowired
    private CurrencyService currencyService;
	
	
    @GetMapping("/banktransaction")
    public String getBanktransaction(Model model) { 
    	logger.info("GET: /banktransaction");
        model.addAttribute("user", userService.getConnectedUser());//list of transactions + preferred currency
        model.addAttribute("currencies", currencyService.findAll()); //list of accepted currencies in db

        return "banktransaction";
    }

    /*
    @PostMapping("/banktransactionGet")
    public String postBanktransactionGetMoney() {//@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
    	logger.info("POST: /banktransactionGet");

    	
    	if (bindingResult.hasErrors()) {
        	model.addAttribute("currencies", currencyService.findAll()); //list of currencies in database
            return "registration";
        }
        
        if ( userService.existsByEmail(userForm.getEmail()) ) {
        	bindingResult.rejectValue("email", "", "This email already exists");
        	model.addAttribute("currencies", currencyService.findAll()); //list of currencies in database
            return "registration";
        }
        
        //need to save password for autologin, because userForm password will be encoded by userService
        String password = userForm.getPassword(); 
        userService.create(userForm);
       
        securityService.autoLogin(userForm.getEmail(), password);
		
        return "redirect:/";
       
    }
*/	
}
