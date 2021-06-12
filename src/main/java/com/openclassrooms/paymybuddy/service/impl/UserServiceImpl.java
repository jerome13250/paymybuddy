package com.openclassrooms.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.RoleRepository;
import com.openclassrooms.paymybuddy.repositories.UserRepository;
import com.openclassrooms.paymybuddy.service.interfaces.CalculationService;
import com.openclassrooms.paymybuddy.service.interfaces.LocalDateTimeService;
import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.service.interfaces.SecurityService;
import com.openclassrooms.paymybuddy.service.interfaces.UserService;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

@Service
public class UserServiceImpl implements UserService {

	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private SecurityService securityService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private CalculationService calculationService;
	@Autowired
	private LocalDateTimeService localDateTimeService;
	@Autowired
	private PagingService pagingService;

	@Override
	public void create(User user) {
		logger.debug("Calling create(User user)");
		String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		//Setting value is required despite passwordconfirm being Transient, hibernate does a validation and checks @PasswordEquality
		//https://stackoverflow.com/questions/12566298/hibernate-bean-validation-issue :
		//user.setPasswordconfirm(encryptedPassword);
		user.setEnabled(true); //by default new user is enabled
		user.setInscriptiondatetime(localDateTimeService.now()); //time of user creation
		user.setAmount(new BigDecimal(0));
		
		//Create the role for user, by default always create a USER role, not ADMIN...
		HashSet<Role> hashSetRoleUserOnly = new HashSet<>();
		hashSetRoleUserOnly.add(roleRepository.findByRolename("USER"));
		user.setRoles(hashSetRoleUserOnly); 
		
		userRepository.save(user);
	}
	
	@Override
	public void update(User user) {
		//user.setPasswordconfirm(user.getPassword());
		logger.debug("Calling update(User user)");		
		userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public User getCurrentUser() {
		return findByEmail(securityService.getCurrentUserDetailsUserName());
	}
	
	@Override
	public void sumAmount(User user, BigDecimal amount, Currency currency) throws UserAmountException { 
		
		BigDecimal resultAmount = calculationService.sumCurrencies(user.getAmount(), user.getCurrency(), amount, currency);
		if (resultAmount.compareTo(new BigDecimal(0))<0) {
			throw new UserAmountException("InsufficientFunds", "This amount exceeds your account value.");
		}
		if (resultAmount.compareTo(new BigDecimal(9999999))>0) {
			throw new UserAmountException("UserAmountExceedsMax", "Account can not exceed max value allowed.");
		}
		
		user.setAmount(resultAmount);
		update(user);
		
	}

	@Override
	public Paged<User> getCurrentUserConnectionPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> page = userRepository.findConnectionById(getCurrentUser().getId(),request);
        return new Paged<>(page, pagingService.of(page.getTotalPages(), pageNumber));
    }

	@Override
	public User findById(Long id) {
		Optional<User> optuser = userRepository.findById(id);
		return optuser.isEmpty()? null : optuser.get();
		
		
	}
	
	
	
}
