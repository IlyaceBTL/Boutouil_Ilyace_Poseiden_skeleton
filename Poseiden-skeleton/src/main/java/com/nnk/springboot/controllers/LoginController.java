package com.nnk.springboot.controllers;

import com.nnk.springboot.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

/**
 * Authentication and access control related endpoints.
 */
@Controller
public class LoginController {

    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Show login page.
     * @return login view
     */
    @GetMapping("/login")
    public String login() {
        logger.info("Displaying login page");
        return "login";
    }

    /**
     * Display secured article details (users list).
     * @param model view model
     * @return user list view
     */
    @GetMapping("/secure/article-details")
    public String getAllUserArticles(Model model) {
        logger.info("Displaying all user articles");
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    /**
     * Access denied handler.
     * @param model view model
     * @return 403 view
     */
    @GetMapping("/403")
    public String accessDenied(Model model) {
        logger.warn("Access denied - displaying 403 page");
        model.addAttribute("errorMsg", "You are not authorized for the requested resource.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("remoteUser", auth.getName());
        }
        return "403";
    }
}
