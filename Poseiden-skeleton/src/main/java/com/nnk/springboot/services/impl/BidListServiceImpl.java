package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.BidListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Service implementation for managing BidList entities.
 * Provides CRUD operations with logging.
 */
@Service
@Transactional
public class BidListServiceImpl implements BidListService {

    private static final Logger logger = LogManager.getLogger(BidListServiceImpl.class);

    private final BidListRepository repository;

    public BidListServiceImpl(BidListRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieve all BidList entries.
     * @return list of BidList
     */
    @Override
    public List<BidList> findAll() {
        logger.debug("Fetching all BidList entries");
        List<BidList> list = repository.findAll();
        logger.info("Fetched {} BidList entries", list.size());
        return list;
    }

    /**
     * Find a BidList by its id.
     * @param id identifier
     * @return BidList found
     * @throws IllegalArgumentException if not found
     */
    @Override
    public BidList findById(Integer id) {
        logger.debug("Fetching BidList by id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("BidList not found for id={}", id);
                    return new IllegalArgumentException("BidList not fount on the id=" + id);
                });
    }

    /**
     * Create a new BidList (id must be null).
     * @param bid entity to create
     * @return created entity
     * @throws IllegalArgumentException if id already present
     */
    @Override
    public BidList create(BidList bid) {
        logger.debug("Creating new BidList: {}", bid);
        if (bid.getBidListId() != null) {
            logger.warn("Attempt to create BidList with existing id={}", bid.getBidListId());
            throw new IllegalArgumentException("New BidList cannot already have an ID");
        }
        BidList saved = repository.save(bid);
        logger.info("BidList created with id={}", saved.getBidListId());
        return saved;
    }

    /**
     * Save (create or update) a BidList.
     * @param bid entity
     * @return saved entity
     */
    @Override
    public BidList save(BidList bid) {
        logger.debug("Saving (upsert) BidList (may contain id={}): {}", bid.getBidListId(), bid);
        BidList saved = repository.save(bid);
        logger.info("BidList saved id={}", saved.getBidListId());
        return saved;
    }

    /**
     * Update an existing BidList by id.
     * @param id target id
     * @param bid incoming values
     * @return updated entity
     */
    @Override
    public BidList update(Integer id, BidList bid) {
        logger.debug("Updating BidList id={}", id);
        BidList existing = findById(id);
        existing.setAccount(bid.getAccount());
        existing.setType(bid.getType());
        existing.setBidQuantity(bid.getBidQuantity());
        BidList updated = repository.save(existing);
        logger.info("BidList updated id={}", id);
        return updated;
    }

    /**
     * Delete a BidList by id.
     * @param id identifier
     */
    @Override
    public void delete(Integer id) {
        logger.debug("Deleting BidList id={}", id);
        BidList existing = findById(id);
        repository.delete(existing);
        logger.info("BidList deleted id={}", id);
    }
}
