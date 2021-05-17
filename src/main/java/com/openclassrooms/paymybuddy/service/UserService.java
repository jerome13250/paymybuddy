package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
