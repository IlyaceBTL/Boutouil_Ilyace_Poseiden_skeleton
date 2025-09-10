package com.nnk.springboot;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateAndFindUser() {
        User user = new User();
        user.setUsername("serviceuser");
        user.setPassword("Password1!");
        user.setFullname("Service User");
        user.setRole("USER");
        User saved = userService.create(user);
        Assert.assertNotNull(saved.getId());
        User found = userService.findById(saved.getId());
        Assert.assertEquals("serviceuser", found.getUsername());
        userService.delete(saved.getId());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setUsername("updateuser");
        user.setPassword("Password1!");
        user.setFullname("Update User");
        user.setRole("USER");
        User saved = userService.create(user);
        saved.setFullname("Updated Name");
        User updated = userService.update(saved.getId(), saved);
        Assert.assertEquals("Updated Name", updated.getFullname());
        userService.delete(saved.getId());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setUsername("deleteuser");
        user.setPassword("Password1!");
        user.setFullname("Delete User");
        user.setRole("USER");
        User saved = userService.create(user);
        Integer id = saved.getId();
        userService.delete(id);
        try {
            userService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("User not fount"));
        }
    }

    @Test
    public void testFindAllUsers() {
        int initialSize = userService.findAll().size();
        User user = new User();
        user.setUsername("alluser");
        user.setPassword("Password1!");
        user.setFullname("All User");
        user.setRole("USER");
        User saved = userService.create(user);
        List<User> all = userService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        userService.delete(saved.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithExistingUsernameThrowsException() {
        User user1 = new User();
        user1.setUsername("uniqueuser");
        user1.setPassword("Password1!");
        user1.setFullname("Unique User");
        user1.setRole("USER");
        userService.create(user1);

        User user2 = new User();
        user2.setUsername("uniqueuser");
        user2.setPassword("Password1!");
        user2.setFullname("Another User");
        user2.setRole("USER");
        userService.create(user2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithInvalidPasswordThrowsException() {
        User user = new User();
        user.setUsername("badpassworduser");
        user.setPassword("pass"); // trop court, pas de majuscule, pas de chiffre, pas de symbole
        user.setFullname("Bad Password User");
        user.setRole("USER");
        userService.create(user);
    }
}
