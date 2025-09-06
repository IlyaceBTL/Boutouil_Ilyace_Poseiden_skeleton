package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

/**
 * Controller for User management (list/create/update/delete).
 */
@Controller
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Display user list.
     * @param model view model
     * @return list view
     */
    @RequestMapping("/user/list")
    public String home(Model model)
    {
        logger.info("Displaying user list");
        model.addAttribute("users", userRepository.findAll());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "user/list";
    }

    /**
     * Show add user form.
     * @param model view model
     * @return add view
     */
    @GetMapping("/user/add")
    public String addUser(Model model) {
        logger.info("Displaying add user form");
        model.addAttribute("user", new User());
        return "user/add";
    }

    /**
     * Validate and create user.
     * @param user entity
     * @param result validation result
     * @param model view model
     * @return redirect or add view
     */
    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, Model model) {
        logger.info("Validating new user");
        if (!result.hasErrors()) {
            try {
                userService.create(user);
                logger.info("User created successfully");
            } catch (IllegalArgumentException ex) {
                logger.warn("User creation failed: {}", ex.getMessage());
                result.rejectValue("password", "error.user", ex.getMessage());
                return "user/add";
            }
            model.addAttribute("users", userService.findAll());
            return "redirect:/user/list";
        }
        logger.warn("User validation failed");
        return "user/add";
    }

    /**
     * Show update form.
     * @param id user id
     * @param model view model
     * @return update view
     */
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for user id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword("");
        model.addAttribute("user", user);
        return "user/update";
    }

    /**
     * Update user.
     * @param id user id
     * @param user updated data
     * @param result validation result
     * @param model view model
     * @return redirect or update view
     */
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid User user,
                             BindingResult result, Model model) {
        logger.info("Updating user id: {}", id);
        if (result.hasErrors()) {
            logger.warn("User update validation failed for id: {}", id);
            return "user/update";
        }
        try {
            userService.update(id, user);
            logger.info("User updated successfully for id: {}", id);
        } catch (IllegalArgumentException ex) {
            logger.warn("User update failed for id {}: {}", id, ex.getMessage());
            result.rejectValue("password", "error.user", ex.getMessage());
            return "user/update";
        }
        model.addAttribute("users", userService.findAll());
        return "redirect:/user/list";
    }

    /**
     * Delete user.
     * @param id user id
     * @param model view model
     * @return redirect
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        logger.info("Deleting user id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        logger.info("User deleted successfully for id: {}", id);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }
}
