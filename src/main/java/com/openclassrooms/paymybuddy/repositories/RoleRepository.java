package com.openclassrooms.paymybuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByRolename(String rolename); 
}
