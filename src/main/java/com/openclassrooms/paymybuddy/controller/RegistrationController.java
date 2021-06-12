package com.openclassrooms.paymybuddy.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserTransaction;
import com.openclassrooms.paymybuddy.model.dto.UserFormDTO;
import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;

@Controller
public class RegistrationController {

	Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
    private UserService userService;
	@Autowired
    private SecurityService securityService;
	//https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
	@Autowired
	private ModelMapper modelMapper;
	
    @GetMapping("/registration")
    public String registration(Model model) { 
    	logger.info("GET: /registration");
    	model.addAttribute("userForm", new UserFormDTO());
        
        return "registration";
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") UserFormDTO userFormDTO, BindingResult bindingResult, Model model) {
    	logger.info("POST: /registration");
    	
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        
        if ( userService.existsByEmail(userFormDTO.getEmail()) ) {
        	bindingResult.rejectValue("email", "", "This email already exists");
            return "registration";
        }
        
        User user = convertToEntity(userFormDTO);
        
        //need to save password for autologin, because userForm password will be encoded by userService
        //String password = userForm.getPassword(); //TODO : remove
        userService.create(user);
       
        securityService.autoLogin(userFormDTO.getEmail(), userFormDTO.getPassword());

        return "redirect:/";
    }
    
    /**
     * This method converts a DTO object to an Entity
     * 
     * @param userFormDTO
     * @return Entity version of the DTO
     * 
     * @see <a href="https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application"> Entity/DTO conversion
     */
    private User convertToEntity(UserFormDTO userFormDTO) {
    	User user = modelMapper.map(userFormDTO, User.class);

        return user;
    }
	
}
