package com.nnk.springboot.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsLogger {
    // Logger instance for this class
    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventsLogger.class);

    /**
     * Logs successful authentication events.
     * @param event the authentication success event
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("Successful connection for : {}", event.getAuthentication().getName());
    }

    /**
     * Logs failed authentication events.
     * @param event the authentication failure event
     */
    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("Connection failure for : {}", event.getAuthentication().getName());
    }

    /**
     * Logs successful logout events.
     * @param event the logout success event
     */
    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("Successful disconnection for : {}", username);
    }
}