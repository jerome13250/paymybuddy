package com.openclassrooms.paymybuddy.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.openclassrooms.paymybuddy.config.CurrenciesAllowed;
import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.dto.UserTransactionFormDTO;
import com.openclassrooms.paymybuddy.service.interfaces.UserTransactionService;
import com.openclassrooms.paymybuddy.service.interfaces.CalculationService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@Controller
public class UserTransactionController {

	Logger logger = LoggerFactory.getLogger(UserTransactionController.class);

	@Autowired
    private UserService userService;
	@Autowired
    private UserTransactionService userTransactionService;
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Autowired
    private ModelMapper modelMapper;
	@Autowired
    private CurrenciesAllowed currenciesAllowed;
	@Autowired
	private CalculationService calculationService;
	
    @GetMapping("/usertransaction")
    public String getUsertransaction(
    		@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size, 
            Model model) { 
    	logger.info("GET: /usertransaction");
    	
    	User user = userService.getCurrentUser();
        model.addAttribute("user", user);//needed to display current user amount + currency
        model.addAttribute("paged", userTransactionService.getCurrentUserUserTransactionPage(pageNumber, size));
        
        UserTransactionFormDTO userTransactionFormDTO = new UserTransactionFormDTO();
        userTransactionFormDTO.setCurrency(user.getCurrency()); //sets by default the form currency to currency of the user.
        model.addAttribute("usertransactionFormDTO",userTransactionFormDTO);
        return "usertransaction";
    }
    
    @Transactional
    @PostMapping("/usertransaction")
    public String postUsertransactionGetMoney(
    		@Valid @ModelAttribute("usertransactionFormDTO") UserTransactionFormDTO userTransactionFormDTO, 
    		BindingResult bindingResult, 
    		Model model) {
    	
    	logger.info("POST: /usertransaction");
    	User sourceUser = userService.getCurrentUser();
    	model.addAttribute("user", sourceUser);//list of transactions + preferred currency
    	model.addAttribute("paged", userTransactionService.getCurrentUserUserTransactionPage(1, 5));
    	
    	if (bindingResult.hasErrors()) {        	
            return "usertransaction";
        }
    	
    	logger.debug("Check userDestination belongs to buddies list");
    	//Check userDestination belongs to buddies list:
        if ( !sourceUser.getConnections().contains(userTransactionFormDTO.getUserDestination()) ) {
        	logger.debug("Failure: unknown buddy");
        	bindingResult.rejectValue("userDestination", "userDestinationNotABuddy", "Please select a buddy !");
        	return "usertransaction";
        }
    	
        logger.debug("Check UnknownCurrency");
    	//UnknownCurrency
        if ( !currenciesAllowed.getCurrenciesAllowedList().contains(userTransactionFormDTO.getCurrency()) ) {
        	bindingResult.rejectValue("currency", "UnknownCurrency", "This currency is not allowed.");
        	return "usertransaction";
        }
    	
        UserTransaction userTransaction = convertToEntity(userTransactionFormDTO);
        
    	//calculate fees:
        Map<String, BigDecimal> feesMap = calculationService.calculateFees(userTransaction.getAmount());
        
        //update user amount:
        try {
			userService.sumAmount(
					sourceUser, 
					userTransaction.getAmount().negate(), //Must be negative for sourceUser 
					userTransaction.getCurrency()
					);
			userService.sumAmount(
					userTransactionFormDTO.getUserDestination(),
					feesMap.get("finalAmount"),  
					userTransaction.getCurrency()
					);
			
		} catch (UserAmountException e) {
			logger.debug("UserAmountException");
			bindingResult.rejectValue("amount", e.getErrorCode(), e.getDefaultMessage());
        	return "usertransaction";
		}
        
              
        //create usertransaction:
        userTransactionService.create(userTransaction, feesMap);
        
        //redirection do not use the current Model, it goes to GET /bantransaction
        return "redirect:/usertransaction";
    }
    
  
    /**
     * This method converts a DTO object to an Entity
     * 
     * @param userTransactionFormDTO
     * @return Entity version of the DTO
     * 
     * @see <a href="https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application"> Entity/DTO conversion
     */
    private UserTransaction convertToEntity(UserTransactionFormDTO userTransactionFormDTO) {
    	UserTransaction userTransaction = modelMapper.map(userTransactionFormDTO, UserTransaction.class);

        return userTransaction;
    }
    
    
	
}
