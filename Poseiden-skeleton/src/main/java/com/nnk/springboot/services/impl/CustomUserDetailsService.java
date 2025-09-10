package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Custom service for retrieving user details,
 * used by Spring Security during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Constructor for CustomUserDetailsService.
     *
     * @param userService the service used to access users
     */
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads user details by username.
     *
     * @param username the username
     * @return the user details for Spring Security
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User domainUser = userService.findByUsername(username);
        if (domainUser == null) {
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        String role = domainUser.getRole();
        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));
        }
        return new org.springframework.security.core.userdetails.User(
                domainUser.getUsername(),
                domainUser.getPassword(),
                authorities
        );
    }
}
