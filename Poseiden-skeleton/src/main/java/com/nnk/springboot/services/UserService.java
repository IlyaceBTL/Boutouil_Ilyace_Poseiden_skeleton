package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User findByUsername(String username);
    User create(User user);
    User update(Integer id, User user);
    void delete(Integer id);
}

