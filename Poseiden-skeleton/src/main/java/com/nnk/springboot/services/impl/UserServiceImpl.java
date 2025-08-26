package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank() || !role.equals("ADMIN")) {
            return "USER";
        }
        return "ADMIN";
    }

    private boolean isPasswordValid(String password) {

        return password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"',.<>/?]).{8,}$");
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not fount on the id=" + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (!isPasswordValid(user.getPassword())) {
            throw new IllegalArgumentException("The password must contain at least 8 characters, one uppercase letter, one digit, and one symbol.");
        }
        user.setRole(normalizeRole(user.getRole()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(Integer id, User incoming) {
        User existing = findById(id);
        if (incoming.getFullname() != null) existing.setFullname(incoming.getFullname());
        existing.setRole(normalizeRole(incoming.getRole()));
        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            if (!isPasswordValid(incoming.getPassword())) {
                throw new IllegalArgumentException("The password must contain at least 8 characters, one uppercase letter, one digit, and one symbol.");
            }
            existing.setPassword(passwordEncoder.encode(incoming.getPassword()));
        }
        return userRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        User existing = findById(id);
        userRepository.delete(existing);
    }
}
