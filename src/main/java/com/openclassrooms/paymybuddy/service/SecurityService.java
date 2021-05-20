package com.openclassrooms.paymybuddy.service;

import org.springframework.security.core.userdetails.User;

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
    public String getCurrentUserDetailsUserName();
}
