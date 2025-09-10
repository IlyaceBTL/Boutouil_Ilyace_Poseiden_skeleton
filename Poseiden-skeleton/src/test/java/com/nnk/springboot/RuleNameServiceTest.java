package com.nnk.springboot;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RuleNameServiceTest {

    @Autowired
    private RuleNameService ruleNameService;

    @Test
    public void testCreateAndFindRuleName() {
        RuleName rule = new RuleName("Name", "Desc", "Json", "Template", "SqlStr", "SqlPart");
        RuleName saved = ruleNameService.create(rule);
        Assert.assertNotNull(saved.getId());
        RuleName found = ruleNameService.findById(saved.getId());
        Assert.assertEquals("Name", found.getName());
        ruleNameService.delete(saved.getId());
    }

    @Test
    public void testUpdateRuleName() {
        RuleName rule = new RuleName("Name", "Desc", "Json", "Template", "SqlStr", "SqlPart");
        RuleName saved = ruleNameService.create(rule);
        saved.setDescription("Updated Desc");
        RuleName updated = ruleNameService.update(saved.getId(), saved);
        Assert.assertEquals("Updated Desc", updated.getDescription());
        ruleNameService.delete(saved.getId());
    }

    @Test
    public void testDeleteRuleName() {
        RuleName rule = new RuleName("Name", "Desc", "Json", "Template", "SqlStr", "SqlPart");
        RuleName saved = ruleNameService.create(rule);
        Integer id = saved.getId();
        ruleNameService.delete(id);
        try {
            ruleNameService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("RuleName not fount"));
        }
    }

    @Test
    public void testFindAllRuleNames() {
        int initialSize = ruleNameService.findAll().size();
        RuleName rule = new RuleName("Name", "Desc", "Json", "Template", "SqlStr", "SqlPart");
        RuleName saved = ruleNameService.create(rule);
        List<RuleName> all = ruleNameService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        ruleNameService.delete(saved.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdThrowsException() {
        ruleNameService.findById(-1);
    }
}
