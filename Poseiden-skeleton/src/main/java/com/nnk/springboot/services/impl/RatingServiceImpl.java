package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private static final Logger logger = LogManager.getLogger(RatingServiceImpl.class);

    private final RatingRepository repository;

    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rating> findAll() {
        logger.debug("Fetching all ratings");
        List<Rating> list = repository.findAll();
        logger.info("Fetched {} ratings", list.size());
        return list;
    }

    @Override
    public Rating findById(Integer id) {
        logger.debug("Fetching rating id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Rating not found id={}", id);
                    return new IllegalArgumentException("Rating not fount on the id=" + id);
                });
    }

    @Override
    public Rating create(Rating rating) {
        logger.debug("Creating rating");
        if (rating.getId() != null) {
            logger.warn("Attempt to create rating with existing id={}", rating.getId());
            throw new IllegalArgumentException("New Rating cannot already have an ID");
        }
        Rating saved = repository.save(rating);
        logger.info("Rating created id={}", saved.getId());
        return saved;
    }

    @Override
    public Rating save(Rating rating) {
        logger.debug("Saving (upsert) rating id={}", rating.getId());
        Rating saved = repository.save(rating);
        logger.info("Rating saved id={}", saved.getId());
        return saved;
    }

    @Override
    public Rating update(Integer id, Rating incoming) {
        logger.debug("Updating rating id={}", id);
        Rating existing = findById(id);
        if (incoming.getMoodysRating() != null) existing.setMoodysRating(incoming.getMoodysRating());
        if (incoming.getSandPRating() != null) existing.setSandPRating(incoming.getSandPRating());
        if (incoming.getFitchRating() != null) existing.setFitchRating(incoming.getFitchRating());
        if (incoming.getOrderNumber() != null) existing.setOrderNumber(incoming.getOrderNumber());
        Rating updated = repository.save(existing);
        logger.info("Rating updated id={}", id);
        return updated;
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Deleting rating id={}", id);
        repository.delete(findById(id));
        logger.info("Rating deleted id={}", id);
    }
}
