package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String normalizeRole(String role) {
        String normalized = (role == null || role.isBlank() || !role.equals("ADMIN")) ? "USER" : "ADMIN";
        logger.debug("Normalizing role '{}' -> '{}'", role, normalized);
        return normalized;
    }

    private boolean isPasswordValid(String password) {
        boolean valid = password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"',.<>/?]).{8,}$");
        if (!valid) logger.debug("Password validation failed");
        return valid;
    }

    @Override
    public List<User> findAll() {
        logger.debug("Fetching all users");
        List<User> list = userRepository.findAll();
        logger.info("Fetched {} users", list.size());
        return list;
    }

    @Override
    public User findById(Integer id) {
        logger.debug("Fetching user id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found id={}", id);
                    return new IllegalArgumentException("User not fount on the id=" + id);
                });
    }

    @Override
    public User findByUsername(String username) {
        logger.debug("Fetching user by username={}", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User create(User user) {
        logger.debug("Creating user username={}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            logger.warn("Password missing for username={}", user.getUsername());
            throw new IllegalArgumentException("Password is required");
        }
        if (!isPasswordValid(user.getPassword())) {
            logger.warn("Password policy violation for username={}", user.getUsername());
            throw new IllegalArgumentException("The password must contain at least 8 characters, one uppercase letter, one digit, and one symbol.");
        }
        user.setRole(normalizeRole(user.getRole()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        logger.info("User created id={} username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    @Override
    public User update(Integer id, User incoming) {
        logger.debug("Updating user id={}", id);
        User existing = findById(id);
        if (incoming.getFullname() != null) existing.setFullname(incoming.getFullname());
        existing.setRole(normalizeRole(incoming.getRole()));
        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            if (!isPasswordValid(incoming.getPassword())) {
                logger.warn("Password policy violation on update id={}", id);
                throw new IllegalArgumentException("The password must contain at least 8 characters, one uppercase letter, one digit, and one symbol.");
            }
            existing.setPassword(passwordEncoder.encode(incoming.getPassword()));
        }
        User updated = userRepository.save(existing);
        logger.info("User updated id={}", id);
        return updated;
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Deleting user id={}", id);
        User existing = findById(id);
        userRepository.delete(existing);
        logger.info("User deleted id={}", id);
    }
}
