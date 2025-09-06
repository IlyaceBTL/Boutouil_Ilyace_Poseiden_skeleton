package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import java.util.List;

public interface BidListService {
    List<BidList> findAll();
    BidList findById(Integer id);
    BidList create(BidList bid);
    BidList save(BidList bid);
    BidList update(Integer id, BidList bid);
    void delete(Integer id);
}

