package com.nnk.springboot.integration;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = { "ADMIN" })
class UserControllerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("Password1!");
        user1.setFullname("fullname1");
        user1.setRole("USER");
        user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("Password2!");
        user2.setFullname("fullname2");
        user2.setRole("ADMIN");
        userRepository.deleteAll();
    }

    @Test
    void testGetUserList() throws Exception {
        userService.create(user1);
        userService.create(user2);

        MvcResult result = mockMvc.perform(get("/user/list"))
            .andExpect(view().name("user/list"))
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddUserForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateUser() throws Exception {
        List<User> found = userRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/user/validate")
            .param("username", user1.getUsername())
            .param("password", user1.getPassword())
            .param("fullname", user1.getFullname())
            .param("role", user1.getRole())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/user/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<User> after = userRepository.findAll();
        assertEquals(1, after.size());
        User userTest = after.get(0);
        assertEquals(user1.getUsername(), userTest.getUsername());
        assertEquals(user1.getFullname(), userTest.getFullname());
        assertEquals(user1.getRole(), userTest.getRole());
    }

    @Test
    void testValidateUserErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/user/validate")
            .param("username", "")
            .param("password", user1.getPassword())
            .param("fullname", user1.getFullname())
            .param("role", user1.getRole())
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("user/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<User> after = userRepository.findAll();
        assertTrue(after.isEmpty());
    }

    @Test
    void testShowUpdateForm() throws Exception {
        User saved = userService.create(user1);
        int userId = saved.getId();

        MvcResult result = mockMvc.perform(get("/user/update/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(view().name("user/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateUser() throws Exception {
        User saved = userService.create(user1);
        int userId = saved.getId();

        String newPassword = "NewPassword1!";

        MvcResult result = mockMvc.perform(post("/user/update/{id}", userId)
            .param("id", String.valueOf(userId))
            .param("username", saved.getUsername())
            .param("password", newPassword)
            .param("fullname", saved.getFullname())
            .param("role", saved.getRole())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/user/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        User updated = userService.findById(userId);
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }

    @Test
    void testUpdateUserErrorForm() throws Exception {
        User saved = userService.create(user1);
        int userId = saved.getId();

        MvcResult result = mockMvc.perform(post("/user/update/{id}", userId)
            .param("id", String.valueOf(userId))
            .param("username", "")
            .param("password", saved.getPassword())
            .param("fullname", saved.getFullname())
            .param("role", saved.getRole())
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("user/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        User userTest = userService.findById(userId);
        assertEquals(user1.getUsername(), userTest.getUsername());
    }

    @Test
    void testDeleteUser() throws Exception {
        User saved = userService.create(user1);
        int userId = saved.getId();

        MvcResult result = mockMvc.perform(get("/user/delete/{id}", userId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/user/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<User> after = userService.findAll();
        assertTrue(after.isEmpty());
    }
}
