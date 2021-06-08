package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openclassrooms.paymybuddy.model.UserTransaction;

public interface UserTransactionRepository  extends JpaRepository<UserTransaction, Long>{
	

	@Query(value = 
			"SELECT "
			+ "	t.id, "
			+ "	t.usersource_id, "
			+ "	t.userdestination_id, "
			+ "	t.dateTime, "
			+ "	CASE t.usersource_id WHEN :userid THEN t.amount ELSE t.amount*-1 END AS amount, "
			+ "	t.currency, "
			+ "	CASE t.usersource_id WHEN :userid THEN t.fees ELSE NULL END AS fees "
			+ "FROM transactions_user t "
			+ "where t.usersource_id = :userid OR t.userdestination_id = :userid"
			,
			nativeQuery = true)
	public Page<UserTransaction> findUserTransactionByUserId(@Param("userid") Long userid, Pageable pageRequest);
	
}
