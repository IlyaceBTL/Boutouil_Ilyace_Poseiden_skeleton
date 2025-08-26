package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rating> findAll() {
        return repository.findAll();
    }

    @Override
    public Rating findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rating not fount on the id=" + id));
    }

    @Override
    public Rating create(Rating rating) {
        if (rating.getId() != null) {
            throw new IllegalArgumentException("New Rating cannot already have an ID");
        }
        return repository.save(rating);
    }

    @Override
    public Rating save(Rating rating) {
        return repository.save(rating);
    }

    @Override
    public Rating update(Integer id, Rating incoming) {
        Rating existing = findById(id);
        if (incoming.getMoodysRating() != null) existing.setMoodysRating(incoming.getMoodysRating());
        if (incoming.getSandPRating() != null) existing.setSandPRating(incoming.getSandPRating());
        if (incoming.getFitchRating() != null) existing.setFitchRating(incoming.getFitchRating());
        if (incoming.getOrderNumber() != null) existing.setOrderNumber(incoming.getOrderNumber());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repository.delete(findById(id));
    }
}
