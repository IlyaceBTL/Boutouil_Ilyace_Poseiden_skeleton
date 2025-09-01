package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.RuleNameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RuleNameServiceImpl implements RuleNameService {

    private static final Logger logger = LogManager.getLogger(RuleNameServiceImpl.class);
    private final RuleNameRepository repository;

    public RuleNameServiceImpl(RuleNameRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RuleName> findAll() {
        logger.debug("Fetching all RuleName entries");
        List<RuleName> list = repository.findAll();
        logger.info("Fetched {} RuleName entries", list.size());
        return list;
    }

    @Override
    public RuleName findById(Integer id) {
        logger.debug("Fetching RuleName id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("RuleName not found id={}", id);
                    return new IllegalArgumentException("RuleName not fount on the id=" + id);
                });
    }

    @Override
    public RuleName create(RuleName ruleName) {
        logger.debug("Creating RuleName");
        RuleName saved = repository.save(ruleName);
        logger.info("RuleName created id={}", saved.getId());
        return saved;
    }

    @Override
    public RuleName update(Integer id, RuleName incoming) {
        logger.debug("Updating RuleName id={}", id);
        RuleName existing = findById(id);
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getJson() != null) existing.setJson(incoming.getJson());
        if (incoming.getTemplate() != null) existing.setTemplate(incoming.getTemplate());
        if (incoming.getSqlStr() != null) existing.setSqlStr(incoming.getSqlStr());
        if (incoming.getSqlPart() != null) existing.setSqlPart(incoming.getSqlPart());
        RuleName updated = repository.save(existing);
        logger.info("RuleName updated id={}", id);
        return updated;
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Deleting RuleName id={}", id);
        repository.delete(findById(id));
        logger.info("RuleName deleted id={}", id);
    }
}
