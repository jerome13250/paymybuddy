package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.paymybuddy.model.Role;

/**
 * Spring Data JPA Repository for Role
 * @author jerome
 *
 */

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByRolename(String rolename); 
}
