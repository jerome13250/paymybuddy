package com.openclassrooms.paymybuddy.controller;

import java.math.BigDecimal;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankTransactionService;
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
	@Autowired
    private BankTransactionService bankTransactionService;
	
    @GetMapping("/banktransaction")
    public String getBanktransaction(Model model) { 
    	logger.info("GET: /banktransaction");
        model.addAttribute("user", userService.getConnectedUser());//list of transactions + preferred currency
        model.addAttribute("currencies", currencyService.findAll()); //list of accepted currencies in db
        model.addAttribute("banktransaction",new BankTransaction());

        return "banktransaction";
    }
    
    @PostMapping("/banktransactionGetmoney")
    public String postBanktransactionGetMoney(
    		@Valid @ModelAttribute("banktransaction") BankTransaction bankTransaction, 
    		BindingResult bindingResult, 
    		Model model) {
    	
    	logger.info("POST: /banktransactionGetmoney");
    	model.addAttribute("currencies", currencyService.findAll()); //list of currencies in database
    	User connectedUser = userService.getConnectedUser();
    	model.addAttribute("user", connectedUser);//list of transactions + preferred currency
    	
    	if (bindingResult.hasErrors()) {        	
            return "redirect:/banktransaction";
        }
        
        if ( bankTransaction.getAmount().compareTo(connectedUser.getAmount())>0 ) { //FIXME
        	bindingResult.rejectValue("amount", "", "This amount exceeds your account value.");
            return "redirect:/banktransaction";
        }
        
        //FIXME:need transactional
        connectedUser.setAmount(connectedUser.getAmount().add(bankTransaction.getAmount()));
        userService.update(connectedUser);
        bankTransactionService.create(bankTransaction);
        model.addAttribute("user", userService.getConnectedUser());//list of transactions + preferred currency

        return "redirect:/banktransaction";
    }
	
}
