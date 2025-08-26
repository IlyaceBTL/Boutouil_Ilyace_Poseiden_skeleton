package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.TradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class TradeServiceImpl implements TradeService {

    private final TradeRepository repository;

    public TradeServiceImpl(TradeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Trade> findAll() {
        return repository.findAll();
    }

    @Override
    public Trade findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trade not fount on the id=" + id));
    }

    @Override
    public Trade save(Trade trade) {
        if (trade.getCreationDate() == null) {
            trade.setCreationDate(Timestamp.from(Instant.now()));
        }
        return repository.save(trade);
    }

    @Override
    public Trade update(Integer id, Trade incoming) {
        Trade existing = findById(id);
        if (incoming.getAccount() != null) existing.setAccount(incoming.getAccount());
        if (incoming.getType() != null) existing.setType(incoming.getType());
        if (incoming.getBuyQuantity() != null) existing.setBuyQuantity(incoming.getBuyQuantity());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repository.delete(findById(id));
    }
}
