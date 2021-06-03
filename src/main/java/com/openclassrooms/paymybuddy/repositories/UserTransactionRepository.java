package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openclassrooms.paymybuddy.model.UserTransaction;

public interface UserTransactionRepository  extends JpaRepository<UserTransaction, Long>{
	
	@Query(value = 
			"SELECT * "
			+ "FROM transactions_user t "
			+ "WHERE t.usersource_id = :userid",
			nativeQuery = true)
	public Page<UserTransaction> findUserTransactionByUserId(@Param("userid") Long userid, Pageable pageRequest);
	
}
