package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openclassrooms.paymybuddy.model.UserTransaction;

/**
 * Spring Data JPA Repository for UserTransaction
 * @author jerome
 *
 */


public interface UserTransactionRepository  extends JpaRepository<UserTransaction, Long>{
	

	@Query(value = 
			"SELECT * "
			+ "FROM transactions_user "
			+ "WHERE usersource_id = :usersourceid OR userdestination_id = :usersourceid "
			,
			nativeQuery = true)
	public Page<UserTransaction> findUserTransactionByUserId(@Param("usersourceid") Long usersourceid, Pageable pageRequest);
	
}
