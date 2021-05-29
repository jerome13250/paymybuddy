package com.openclassrooms.paymybuddy.service;

import java.math.BigDecimal;
import java.util.Currency;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.User;

public interface UserService {
    void create(User user);
    void update(User user);
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User getConnectedUser();
    void updateAmount(User user, BigDecimal amount, Currency currency) throws UserAmountException;
    
}
