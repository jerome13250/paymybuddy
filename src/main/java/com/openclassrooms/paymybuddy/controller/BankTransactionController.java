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

import com.openclassrooms.paymybuddy.config.CurrenciesAllowed;
import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.dto.BankTransactionFormDTO;
import com.openclassrooms.paymybuddy.service.BankTransactionService;
import com.openclassrooms.paymybuddy.service.UserService;

@Controller
public class BankTransactionController {

	Logger logger = LoggerFactory.getLogger(BankTransactionController.class);

	@Autowired
    private UserService userService;
	@Autowired
    private BankTransactionService bankTransactionService;
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Autowired
    private ModelMapper modelMapper;
	@Autowired
    private CurrenciesAllowed currenciesAllowed;
	
    @GetMapping("/banktransaction")
    public String getBanktransaction(
    		@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size, 
            Model model) { 
    	logger.info("GET: /banktransaction");
    	
    	User user = userService.getCurrentUser();
        model.addAttribute("user", user);//needed to display current user amount + currency
        model.addAttribute("paged", bankTransactionService.getCurrentUserBankTransactionPage(pageNumber, size));
        
        BankTransactionFormDTO bankTransactionFormDTO = new BankTransactionFormDTO();
        bankTransactionFormDTO.setCurrency(user.getCurrency()); //sets by default the form currency to currency of the user.
        bankTransactionFormDTO.setGetOrSendRadioOptions("send"); //sets by default the form GetOrSendRadioOptions to "send".
        model.addAttribute("banktransactionFormDTO",bankTransactionFormDTO);
        return "banktransaction";
    }
    
    //FIXME:need transactional
    @PostMapping("/banktransaction")
    public String postBanktransactionGetMoney(
    		@Valid @ModelAttribute("banktransactionFormDTO") BankTransactionFormDTO bankTransactionFormDTO, 
    		BindingResult bindingResult, 
    		Model model) {
    	
    	logger.info("POST: /banktransaction");
    	User connectedUser = userService.getCurrentUser();
    	model.addAttribute("user", connectedUser);//list of transactions + preferred currency
    	model.addAttribute("paged", bankTransactionService.getCurrentUserBankTransactionPage(1, 5));
    	
    	if (bindingResult.hasErrors()) {        	
            return "banktransaction";
        }
    	
    	//UnknownCurrency
        if ( !currenciesAllowed.getCurrenciesAllowedList().contains(bankTransactionFormDTO.getCurrency()) ) {
        	bindingResult.rejectValue("currency", "UnknownCurrency", "This currency is not allowed.");
        	return "banktransaction";
        }
    	
        BankTransaction bankTransaction = convertToEntity(bankTransactionFormDTO);
        
    	
        //update user amount:
        try {
			userService.updateAmount(connectedUser, bankTransaction.getAmount(), bankTransaction.getCurrency());
		} catch (UserAmountException e) {
			logger.debug("UserAmountException");
			bindingResult.rejectValue("amount", e.getErrorCode(), e.getDefaultMessage());
        	return "banktransaction";
		}
        
        //create banktransaction:
        bankTransactionService.create(bankTransaction);
        
        //redirection do not use the current Model, it goes to GET /bantransaction
        return "redirect:/banktransaction";
    }
    
    /**
     * This method converts an Entity object to a DTO
     * 
     * @param bankTransaction
     * @return DTO version
     * 
     * @see <a href="https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application"> Entity/DTO conversion
     */
/*    private BankTransactionFormDTO convertToDto(BankTransaction bankTransaction) {
    	BankTransactionFormDTO bankTransactionFormDTO = modelMapper.map(bankTransaction, BankTransactionFormDTO.class);
        return bankTransactionFormDTO;
    }
*/
    
    /**
     * This method converts a DTO object to an Entity
     * 
     * @param bankTransactionFormDTO
     * @return Entity version of the DTO
     * 
     * @see <a href="https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application"> Entity/DTO conversion
     */
    private BankTransaction convertToEntity(BankTransactionFormDTO bankTransactionFormDTO) {
    	BankTransaction bankTransaction = modelMapper.map(bankTransactionFormDTO, BankTransaction.class);
        
    	//If money sent to bank then amount becomes negative:
    	if (bankTransactionFormDTO.getGetOrSendRadioOptions().equalsIgnoreCase("send")) {
    		bankTransaction.setAmount(bankTransactionFormDTO.getAmount().negate());
    	}
        return bankTransaction;
    }
    
    
	
}
