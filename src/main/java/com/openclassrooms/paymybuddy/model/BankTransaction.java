package com.openclassrooms.paymybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions_bank")
public class BankTransaction {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//EAGER fetching is very bad from a performance perspective : https://vladmihalcea.com/eager-fetching-is-a-code-smell/
	//Unfortunately, JPA 1.0 decided that @ManyToOne and @OneToOne should default to FetchType.EAGER , so now you have to
	//explicitly mark these two associations as FetchType.LAZY.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	private String bankaccountnumber;
	
	private LocalDateTime datetime;
	
	private BigDecimal amount;
	
	private java.util.Currency currency;

}
