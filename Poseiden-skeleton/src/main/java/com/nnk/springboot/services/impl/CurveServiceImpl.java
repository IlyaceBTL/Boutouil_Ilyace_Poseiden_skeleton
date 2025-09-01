package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.CurveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CurveServiceImpl implements CurveService {

    private static final Logger logger = LogManager.getLogger(CurveServiceImpl.class);

    private final CurvePointRepository repository;

    public CurveServiceImpl(CurvePointRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CurvePoint> findAll() {
        logger.debug("Fetching all curve points");
        List<CurvePoint> list = repository.findAll();
        logger.info("Fetched {} curve points", list.size());
        return list;
    }

    @Override
    public CurvePoint findById(Integer id) {
        logger.debug("Fetching curve point id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("CurvePoint not found id={}", id);
                    return new IllegalArgumentException("CurvePoint not fount on the id=" + id);
                });
    }

    @Override
    public CurvePoint create(CurvePoint curvePoint) {
        logger.debug("Creating curve point");
        Timestamp now = Timestamp.from(Instant.now());
        if (curvePoint.getCreationDate() == null) {
            curvePoint.setCreationDate(now);
        }
        if (curvePoint.getAsOfDate() == null) {
            curvePoint.setAsOfDate(now);
        }
        CurvePoint saved = repository.save(curvePoint);
        logger.info("Curve point created id={}", saved.getId());
        return saved;
    }

    @Override
    public CurvePoint update(Integer id, CurvePoint curvePointUpdate) {
        logger.debug("Updating curve point id={}", id);
        Timestamp now = Timestamp.from(Instant.now());
        CurvePoint existing = findById(id);
        if (curvePointUpdate.getCurveId() != null) existing.setCurveId(curvePointUpdate.getCurveId());
        if (curvePointUpdate.getTerm() != null) existing.setTerm(curvePointUpdate.getTerm());
        if (curvePointUpdate.getValue() != null) existing.setValue(curvePointUpdate.getValue());
        existing.setAsOfDate(now);
        CurvePoint updated = repository.save(existing);
        logger.info("Curve point updated id={}", id);
        return updated;
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Deleting curve point id={}", id);
        repository.delete(findById(id));
        logger.info("Curve point deleted id={}", id);
    }
}
