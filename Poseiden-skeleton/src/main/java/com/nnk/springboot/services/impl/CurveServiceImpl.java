package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.CurveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CurveServiceImpl implements CurveService {

    private final CurvePointRepository repository;

    public CurveServiceImpl(CurvePointRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CurvePoint> findAll() {
        return repository.findAll();
    }

    @Override
    public CurvePoint findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CurvePoint not fount on the id=" + id));
    }

    @Override
    public CurvePoint create(CurvePoint curvePoint) {
        Timestamp now = Timestamp.from(Instant.now());
        if (curvePoint.getCreationDate() == null) curvePoint.setCreationDate(now);
        if (curvePoint.getAsOfDate() == null) curvePoint.setAsOfDate(now);
        return repository.save(curvePoint);
    }

    @Override
    public CurvePoint update(Integer id, CurvePoint curvePointUpdate) {
        Timestamp now = Timestamp.from(Instant.now());
        CurvePoint existing = findById(id);
        if (curvePointUpdate.getCurveId() != null) existing.setCurveId(curvePointUpdate.getCurveId());
        if (curvePointUpdate.getTerm() != null) existing.setTerm(curvePointUpdate.getTerm());
        if (curvePointUpdate.getValue() != null) existing.setValue(curvePointUpdate.getValue());
        existing.setAsOfDate(now);
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repository.delete(findById(id));
    }
}
