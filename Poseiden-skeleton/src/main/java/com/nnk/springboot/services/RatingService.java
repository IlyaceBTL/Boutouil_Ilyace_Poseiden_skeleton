package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import java.util.List;

public interface RatingService {
    List<Rating> findAll();
    Rating findById(Integer id);

    Rating create(Rating rating);

    Rating save(Rating rating);
    Rating update(Integer id, Rating rating);
    void delete(Integer id);
}
