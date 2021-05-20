package com.openclassrooms.paymybuddy.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.openclassrooms.paymybuddy.validation.PasswordEquality;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
@PasswordEquality
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String firstname;
	@NotBlank
	private String lastname;
	@NotBlank
	@Email
	private String email;
	private LocalDateTime inscriptiondatetime;
	@NotBlank
	private String password;
	@Transient
	@NotBlank
	private String passwordconfirm;
	private Boolean enabled;
	@NotBlank
	private String bankaccountnumber;
	private Double amount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency_id")
	private Currency currency;
	
	@ManyToMany
	@JoinTable(
			name = "user_roles", 
			joinColumns = @JoinColumn(name = "users_id"), 
			inverseJoinColumns = @JoinColumn(name = "roles_id"))
	private Set<Role> roles;
	
	@ManyToMany
	@JoinTable(
			name = "user_connections", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "connection_id"))
	private Set<User> connections;
	


}
