package com.nnk.springboot.services.impl;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.RuleNameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RuleNameServiceImpl implements RuleNameService {

    private final RuleNameRepository repository;

    public RuleNameServiceImpl(RuleNameRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RuleName> findAll() {
        return repository.findAll();
    }

    @Override
    public RuleName findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RuleName not fount on the id=" + id));
    }

    @Override
    public RuleName create(RuleName ruleName) {
        return repository.save(ruleName);
    }

    @Override
    public RuleName update(Integer id, RuleName incoming) {
        RuleName existing = findById(id);
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getJson() != null) existing.setJson(incoming.getJson());
        if (incoming.getTemplate() != null) existing.setTemplate(incoming.getTemplate());
        if (incoming.getSqlStr() != null) existing.setSqlStr(incoming.getSqlStr());
        if (incoming.getSqlPart() != null) existing.setSqlPart(incoming.getSqlPart());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repository.delete(findById(id));
    }
}
