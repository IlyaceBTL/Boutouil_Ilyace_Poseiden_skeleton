package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
class RatingControllerIntegrationTest {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MockMvc mockMvc;

    Rating rating1;
    Rating rating2;

    @BeforeEach
    void setUp() {
        rating1 = new Rating("Moodys", "AAA", "Order1", 1);
        rating2 = new Rating("S&P", "BBB", "Order2", 2);
        ratingRepository.deleteAll();
    }

    @Test
    void testGetRatingList() throws Exception {
        ratingService.create(rating1);
        ratingService.create(rating2);

        MvcResult result = mockMvc.perform(get("/rating/list"))
            .andExpect(view().name("rating/list"))
            .andExpect(model().attributeExists("ratings"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddRatingForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/rating/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("rating/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateRating() throws Exception {
        List<Rating> found = ratingRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/rating/validate")
            .param("moodysRating", rating1.getMoodysRating())
            .param("sandPRating", rating1.getSandPRating())
            .param("fitchRating", rating1.getFitchRating())
            .param("orderNumber", rating1.getOrderNumber().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/rating/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<Rating> after = ratingRepository.findAll();
        assertEquals(1, after.size());
        Rating ratingTest = after.get(0);
        assertEquals(rating1.getMoodysRating(), ratingTest.getMoodysRating());
        assertEquals(rating1.getSandPRating(), ratingTest.getSandPRating());
        assertEquals(rating1.getOrderNumber(), ratingTest.getOrderNumber());
    }

    @Test
    void testValidateRatingErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/rating/validate")
            .param("moodysRating", "")
            .param("sandPRating", rating1.getSandPRating())
            .param("fitchRating", rating1.getFitchRating())
            .param("orderNumber", rating1.getOrderNumber().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testShowUpdateForm() throws Exception {
        Rating saved = ratingService.create(rating1);
        int ratingId = saved.getId();

        MvcResult result = mockMvc.perform(get("/rating/update/{id}", ratingId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("rating"))
            .andExpect(view().name("rating/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateRating() throws Exception {
        Rating saved = ratingService.create(rating1);
        int ratingId = saved.getId();

        MvcResult result = mockMvc.perform(post("/rating/update/{id}", ratingId)
            .param("id", String.valueOf(ratingId))
            .param("moodysRating", "UpdatedMoodys")
            .param("sandPRating", saved.getSandPRating())
            .param("fitchRating", saved.getFitchRating())
            .param("orderNumber", saved.getOrderNumber().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/rating/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        Rating updated = ratingService.findById(ratingId);
        assertEquals("UpdatedMoodys", updated.getMoodysRating());
    }

    @Test
    void testUpdateRatingErrorForm() throws Exception {
        Rating saved = ratingService.create(rating1);
        int ratingId = saved.getId();

        MvcResult result = mockMvc.perform(post("/rating/update/{id}", ratingId)
            .param("id", String.valueOf(ratingId))
            .param("moodysRating", "")
            .param("sandPRating", saved.getSandPRating())
            .param("fitchRating", saved.getFitchRating())
            .param("orderNumber", saved.getOrderNumber().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testDeleteRating() throws Exception {
        Rating saved = ratingService.create(rating1);
        int ratingId = saved.getId();

        MvcResult result = mockMvc.perform(get("/rating/delete/{id}", ratingId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/rating/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<Rating> after = ratingService.findAll();
        assertTrue(after.isEmpty());
    }
}
