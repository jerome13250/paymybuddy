package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;

public interface UserService {
    void create(User user);
    void update(User user);
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User getConnectedUser();
}
