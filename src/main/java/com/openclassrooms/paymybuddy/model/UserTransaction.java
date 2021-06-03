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
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions_user")
public class UserTransaction {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)//child entity, owner of the relationship
    @JoinColumn(name = "usersource_id", nullable = false)
    private User userSource;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)//child entity, owner of the relationship
    @JoinColumn(name = "userdestination_id", nullable = false)
    private User userDestination;
	
	private LocalDateTime datetime;
	
	private BigDecimal amount;
	
	private java.util.Currency currency;
	
	private BigDecimal fees;

}
