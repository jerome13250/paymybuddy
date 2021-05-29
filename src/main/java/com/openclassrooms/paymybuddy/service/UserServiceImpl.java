package com.openclassrooms.paymybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repositories.RoleRepository;
import com.openclassrooms.paymybuddy.repositories.UserRepository;

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
	private CalculationCurrencyService calculationCurrencyService;

	@Override
	public void create(User user) {
		logger.debug("Calling create(User user)");
		String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		//Setting value is required despite passwordconfirm being Transient, hibernate does a validation and checks @PasswordEquality
		//https://stackoverflow.com/questions/12566298/hibernate-bean-validation-issue :
		user.setPasswordconfirm(encryptedPassword);
		user.setEnabled(true); //by default new user is enabled
		user.setInscriptiondatetime(LocalDateTime.now()); //time of user creation
		user.setAmount(new BigDecimal(0));
		
		//Create the role for user, by default always create a USER role, not ADMIN...
		HashSet<Role> hashSetRoleUserOnly = new HashSet<>();
		hashSetRoleUserOnly.add(roleRepository.findByRolename("USER"));
		user.setRoles(hashSetRoleUserOnly); 
		
		userRepository.save(user);
	}
	
	@Override
	public void update(User user) {
		user.setPasswordconfirm(user.getPassword()); //FIXME 
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
	public User getConnectedUser() {
		return findByEmail(securityService.getCurrentUserDetailsUserName());
	}

	@Override
	public void updateAmount(User user, BigDecimal amount, Currency currency) throws UserAmountException { 
		
		BigDecimal result = calculationCurrencyService.sumCurrencies(user.getAmount(), user.getCurrency(), amount, currency);
		if (result.compareTo(new BigDecimal(0))==-1) {
			throw new UserAmountException("InsufficientFunds", "This amount exceeds your account value.");
		}
		if (result.compareTo(new BigDecimal(9999999))==1) {
			throw new UserAmountException("UserAmountExceedsMax", "The user amount exceeds Max value.");
		}
		
		user.setAmount(result);
		update(user);
		
	}

}
