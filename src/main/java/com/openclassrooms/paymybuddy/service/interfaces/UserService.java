package com.openclassrooms.paymybuddy.service.interfaces;

import java.math.BigDecimal;
import java.util.Currency;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.utils.paging.Paged;

public interface UserService {
    void create(User user);
    void update(User user);
    User findByEmail(String email);
    User findById(Long id);
    Boolean existsByEmail(String email);
    User getCurrentUser();
    void updateAmount(User user, BigDecimal amount, Currency currency) throws UserAmountException;
    Paged<User> getCurrentUserConnectionPage(int pageNumber, int size);
    
}
