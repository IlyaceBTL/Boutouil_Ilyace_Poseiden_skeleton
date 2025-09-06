package com.nnk.springboot;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    @Test
    public void testCreateAndFindRating() {
        Rating rating = new Rating("Moodys", "SandP", "Fitch", 1);
        Rating saved = ratingService.create(rating);
        Assert.assertNotNull(saved.getId());
        Rating found = ratingService.findById(saved.getId());
        Assert.assertEquals("Moodys", found.getMoodysRating());
        ratingService.delete(saved.getId());
    }

    @Test
    public void testUpdateRating() {
        Rating rating = new Rating("Moodys", "SandP", "Fitch", 2);
        Rating saved = ratingService.create(rating);
        saved.setOrderNumber(99);
        Rating updated = ratingService.update(saved.getId(), saved);
        Assert.assertEquals(Integer.valueOf(99), updated.getOrderNumber());
        ratingService.delete(saved.getId());
    }

    @Test
    public void testDeleteRating() {
        Rating rating = new Rating("Moodys", "SandP", "Fitch", 3);
        Rating saved = ratingService.create(rating);
        Integer id = saved.getId();
        ratingService.delete(id);
        try {
            ratingService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Rating not fount"));
        }
    }

    @Test
    public void testFindAllRatings() {
        int initialSize = ratingService.findAll().size();
        Rating rating = new Rating("Moodys", "SandP", "Fitch", 4);
        Rating saved = ratingService.create(rating);
        List<Rating> all = ratingService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        ratingService.delete(saved.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithIdThrowsException() {
        Rating rating = new Rating("Moodys", "SandP", "Fitch", 5);
        rating.setId(999);
        ratingService.create(rating);
    }
}
