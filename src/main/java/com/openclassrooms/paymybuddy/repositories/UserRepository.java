package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.openclassrooms.paymybuddy.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	//Spring is able to create the query by analyzing the method name
	//https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	User findByEmail(String email); 
}
