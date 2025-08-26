package com.nnk.springboot.controllers;

import com.nnk.springboot.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    private final UserRepository userRepository;
    private static final Logger log = LogManager.getLogger(LoginController.class);

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        log.info("Displaying login page");
        return "login";
    }

    @GetMapping("/secure/article-details")
    public String getAllUserArticles(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("errorMsg", "You are not authorized for the requested resource.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("remoteUser", auth.getName());
        }
        return "403";
    }
}
