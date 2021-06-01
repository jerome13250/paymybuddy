package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openclassrooms.paymybuddy.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	//Spring is able to create the query by analyzing the method name
	//https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	public User findByEmail(String email); 

		
	@Query("SELECT CASE "
			+ "WHEN COUNT(u) > 0 THEN true"
			+ " ELSE false END "
			+ "FROM User u "
			+ "WHERE u.email = :email")
    public Boolean existsByEmail(@Param("email") String email);
	
	@Query(value = 
			"SELECT * "
			+ "FROM user u INNER JOIN user_connections ucon "
			+ "ON u.id = ucon.connection_id "
			+ "WHERE ucon.user_id = :id",
			nativeQuery = true)
	public Page<User> findConnectionById(@Param("id") Long id, Pageable pageRequest);

	
}
