package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.BidListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BidListServiceImpl implements BidListService {

    private final BidListRepository repository;

    public BidListServiceImpl(BidListRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BidList> findAll() {
        return repository.findAll();
    }

    @Override
    public BidList findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BidList not fount on the id=" + id));
    }

    @Override
    public BidList create(BidList bid) {
        if (bid.getBidListId() != null) {
            throw new IllegalArgumentException("New BidList cannot already have an ID");
        }
        return repository.save(bid);
    }
    @Override
    public BidList save(BidList bid) {

        return repository.save(bid);
    }

    @Override
    public BidList update(Integer id, BidList bid) {
        BidList existing = findById(id);
        existing.setAccount(bid.getAccount());
        existing.setType(bid.getType());
        existing.setBidQuantity(bid.getBidQuantity());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        BidList existing = findById(id);
        repository.delete(existing);
    }
}

