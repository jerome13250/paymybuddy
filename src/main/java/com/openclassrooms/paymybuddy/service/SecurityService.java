package com.openclassrooms.paymybuddy.service;

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
}
