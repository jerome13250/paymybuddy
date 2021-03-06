package com.openclassrooms.paymybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity object that represents a User.
 * @author jerome
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
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
	@NotNull
	private Boolean enabled;
	@NotBlank
	private String bankaccountnumber;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Currency currency;
	
	@ManyToMany //FetchType.LAZY by default
	@JoinTable(
			name = "user_roles", 
			joinColumns = @JoinColumn(name = "users_id"), 
			inverseJoinColumns = @JoinColumn(name = "roles_id"))
	private Set<Role> roles;
	
	@ManyToMany //FetchType.LAZY by default
	@JoinTable(
			name = "user_connections", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "connection_id"))
	private Set<User> connections;
	
	@OneToMany(mappedBy = "user", //mappedBy indicates the entity BankTransaction owns the relationship
			fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<BankTransaction> banktransactions;
	
	@OneToMany(mappedBy = "userSource", //mappedBy indicates the entity UserTransaction owns the relationship
			fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<UserTransaction> usertransactions;

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	/**
	 * Equals method is calculated on email cause it is the business key.
	 * <br><br>
	 * See here for explanations: <br>
	 * <a href="https://www.baeldung.com/jpa-entity-equality#3-using-a-business-key">baeldung</a> <br>
	 * 
	 * "Using the JPA entity business key for equals and hashCode is always best choice:"<br>
	 * <a href="https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/">hibernate-facts-equals-and-hashcode</a> <br>
	 * <a href="https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">
	 * equals-and-hashcode-using-the-jpa-entity-identifier</a>
	 * 
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(email, other.email);
	}

	


}
