package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.TradeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Service implementation handling Trade persistence and updates.
 */
@Service
@Transactional
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LogManager.getLogger(TradeServiceImpl.class);

    private final TradeRepository repository;

    public TradeServiceImpl(TradeRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all trades.
     * @return list of trades
     */
    @Override
    public List<Trade> findAll() {
        logger.debug("Fetching all trades");
        List<Trade> list = repository.findAll();
        logger.info("Fetched {} trades", list.size());
        return list;
    }

    /**
     * Find a trade by id.
     * @param id trade id
     * @return trade
     * @throws IllegalArgumentException if not found
     */
    @Override
    public Trade findById(Integer id) {
        logger.debug("Fetching trade id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Trade not found id={}", id);
                    return new IllegalArgumentException("Trade not fount on the id=" + id);
                });
    }

    /**
     * Save (create or update) a trade. Sets creation date if absent.
     * @param trade trade entity
     * @return saved trade
     */
    @Override
    public Trade save(Trade trade) {
        logger.debug("Saving trade (id={})", trade.getTradeId());
        if (trade.getCreationDate() == null) {
            logger.debug("Setting creation date for trade");
            trade.setCreationDate(Timestamp.from(Instant.now()));
        }
        Trade saved = repository.save(trade);
        logger.info("Trade saved id={}", saved.getTradeId());
        return saved;
    }

    /**
     * Update selected mutable fields of a trade.
     * @param id trade id
     * @param incoming values to apply
     * @return updated trade
     */
    @Override
    public Trade update(Integer id, Trade incoming) {
        logger.debug("Updating trade id={}", id);
        Trade existing = findById(id);
        if (incoming.getAccount() != null) existing.setAccount(incoming.getAccount());
        if (incoming.getType() != null) existing.setType(incoming.getType());
        if (incoming.getBuyQuantity() != null) existing.setBuyQuantity(incoming.getBuyQuantity());
        Trade updated = repository.save(existing);
        logger.info("Trade updated id={}", id);
        return updated;
    }

    /**
     * Delete a trade by id.
     * @param id trade id
     */
    @Override
    public void delete(Integer id) {
        logger.debug("Deleting trade id={}", id);
        repository.delete(findById(id));
        logger.info("Trade deleted id={}", id);
    }
}
