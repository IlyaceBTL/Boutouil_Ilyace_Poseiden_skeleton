package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Rating CRUD operations.
 */
@Controller
public class RatingController {

    private static final Logger logger = LogManager.getLogger(RatingController.class);

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {

        this.ratingService = ratingService;
    }

    /**
     * List all ratings.
     * @param model view model
     * @return list view
     */
    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        logger.info("Displaying rating list");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "anonymous");
        model.addAttribute("ratings", ratingService.findAll());
        return "rating/list";
    }

    /**
     * Show add form.
     * @param rating backing object
     * @return add view
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        logger.info("Displaying add rating form");
        return "rating/add";
    }

    /**
     * Validate and create rating.
     * @param rating entity
     * @param result validation result
     * @return redirect or add form view
     */
    @PostMapping("/rating/validate")
    public String validate(@Valid Rating rating, BindingResult result) {
        logger.info("Validating new rating");
        if (result.hasErrors()) {
            logger.warn("Rating validation failed");
            return "rating/add";
        }
        ratingService.create(rating);
        logger.info("Rating created successfully");
        return "redirect:/rating/list";
    }

    /**
     * Show update form.
     * @param id rating id
     * @param model view model
     * @return update view
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for rating id: {}", id);
        Rating rating = ratingService.findById(id);
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    /**
     * Update rating.
     * @param id rating id
     * @param rating updated values
     * @param result validation result
     * @return redirect or update view
     */
    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid Rating rating,
                               BindingResult result) {
        logger.info("Updating rating id: {}", id);
        if (result.hasErrors()) {
            logger.warn("Rating update validation failed for id: {}", id);
            rating.setId(id);
            return "rating/update";
        }
        ratingService.update(id, rating);
        logger.info("Rating updated successfully for id: {}", id);
        return "redirect:/rating/list";
    }

    /**
     * Delete rating.
     * @param id rating id
     * @return redirect
     */
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id) {
        logger.info("Deleting rating id: {}", id);
        ratingService.delete(id);
        logger.info("Rating deleted successfully for id: {}", id);
        return "redirect:/rating/list";
    }
}
