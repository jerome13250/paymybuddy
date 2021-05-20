package com.openclassrooms.paymybuddy.testconfig;

import java.util.Arrays;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

//Configuration classes annotated with @TestConfiguration are excluded from component scanning, therefore we need to import it
//explicitly in every test where we want to @Autowire it. We can do that with the @Import annotation.
@TestConfiguration
public class SpringSecurityWebTestConfig {

    @Bean
    //@Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User("user@company.com", "password", Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("PERM_FOO_READ")
        ));
/*
        User managerUser = new UserImpl("Manager User", "manager@company.com", "password");
        UserActive managerActiveUser = new UserActive(managerUser, Arrays.asList(
                new SimpleGrantedAuthority("ROLE_MANAGER"),
                new SimpleGrantedAuthority("PERM_FOO_READ"),
                new SimpleGrantedAuthority("PERM_FOO_WRITE"),
                new SimpleGrantedAuthority("PERM_FOO_MANAGE")
        ));
*/
        return new InMemoryUserDetailsManager(Arrays.asList(
                basicUser /*, managerActiveUser*/
        ));
    }
}