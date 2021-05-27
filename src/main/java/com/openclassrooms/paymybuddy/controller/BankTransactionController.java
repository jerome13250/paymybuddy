package com.openclassrooms.paymybuddy.controller;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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
import com.openclassrooms.paymybuddy.model.dto.BankTransactionFormDTO;
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
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Autowired
    private ModelMapper modelMapper;
	
    @GetMapping("/banktransaction")
    public String getBanktransaction(Model model) { 
    	logger.info("GET: /banktransaction");
        model.addAttribute("user", userService.getConnectedUser());//list of transactions + preferred currency
        model.addAttribute("currencies", currencyService.findAll()); //list of accepted currencies in db
        model.addAttribute("banktransaction",new BankTransactionFormDTO());

        return "banktransaction";
    }
    
    //FIXME:need transactional
    @PostMapping("/banktransaction")
    public String postBanktransactionGetMoney(
    		@Valid @ModelAttribute("banktransaction") BankTransactionFormDTO bankTransactionFormDTO, 
    		BindingResult bindingResult, 
    		Model model) {
    	
    	logger.info("POST: /banktransaction");
    	model.addAttribute("currencies", currencyService.findAll()); //list of currencies in database
    	User connectedUser = userService.getConnectedUser();
    	model.addAttribute("user", connectedUser);//list of transactions + preferred currency
    	
    	if (bindingResult.hasErrors()) {        	
            return "banktransaction";
        }
        
        if ( bankTransactionFormDTO.getGetOrSendRadioOptions().equals("send") &&
        		bankTransactionFormDTO.getAmount().compareTo(connectedUser.getAmount())>0 ) {
        	bindingResult.rejectValue("amount", "", "This amount exceeds your account value.");
            return "banktransaction";
        }

        BankTransaction bankTransaction = convertToEntity(bankTransactionFormDTO);
        //update user amount:
        connectedUser.setAmount(connectedUser.getAmount().add(bankTransaction.getAmount()));
        userService.update(connectedUser);
        //create banktransaction:
        bankTransactionService.create(bankTransaction);
        model.addAttribute("user", userService.getConnectedUser());//list of transactions + preferred currency

        return "redirect:/banktransaction";
    }
    
    private BankTransactionFormDTO convertToDto(BankTransaction bankTransaction) {
    	BankTransactionFormDTO bankTransactionFormDTO = modelMapper.map(bankTransaction, BankTransactionFormDTO.class);
        
        return bankTransactionFormDTO;
    }
    
    private BankTransaction convertToEntity(BankTransactionFormDTO bankTransactionFormDTO) {
    	BankTransaction bankTransaction = modelMapper.map(bankTransactionFormDTO, BankTransaction.class);
        
    	//If money send to bank then amount becomes negative:
    	if (bankTransactionFormDTO.getGetOrSendRadioOptions().equalsIgnoreCase("send")) {
    		bankTransaction.setAmount(bankTransactionFormDTO.getAmount().negate());
    	}
    	
        /* ??????
        if (bankTransactionFormDTO.getId() != null) {
            Post oldPost = postService.getPostById(bankTransactionFormDTO.getId());
            post.setRedditID(oldPost.getRedditID());
            post.setSent(oldPost.isSent());
        }
        */
        return bankTransaction;
    }
    
    
	
}
