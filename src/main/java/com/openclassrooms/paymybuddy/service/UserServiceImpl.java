package com.openclassrooms.paymybuddy.service;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(User user) {
		logger.debug("Calling save(User user)");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setEnabled(true); //by default new user is enabled
		user.setInscriptiondatetime(LocalDateTime.now()); //time of user creation
		//Create the role for user, by default always create a USER role:
		HashSet<Role> hashSetRoleUserOnly = new HashSet<>();
		hashSetRoleUserOnly.add(roleRepository.findByRolename("USER"));
		user.setRoles(hashSetRoleUserOnly); 
		
		userRepository.save(user);
	}

	@Override
	public User findByUsername(String email) {
		return userRepository.findByEmail(email);
	}
}
