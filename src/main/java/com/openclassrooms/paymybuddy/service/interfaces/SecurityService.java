package com.openclassrooms.paymybuddy.service.interfaces;

/**
 * Service that provides current logged-in user and auto login user after registration 
 * @author jerome
 *
 */

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
    public String getCurrentUserDetailsUserName();
}
