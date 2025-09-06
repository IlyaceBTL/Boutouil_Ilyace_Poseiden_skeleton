package com.nnk.springboot.integration;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.RuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = { "ADMIN" })
class RuleNameControllerIntegrationTest {

    @Autowired
    private RuleNameService ruleNameService;

    @Autowired
    private RuleNameRepository ruleNameRepository;

    @Autowired
    private MockMvc mockMvc;

    RuleName rule1;
    RuleName rule2;

    @BeforeEach
    void setUp() {
        rule1 = new RuleName("Name1", "Desc1", "Json1", "Template1", "SqlStr1", "SqlPart1");
        rule2 = new RuleName("Name2", "Desc2", "Json2", "Template2", "SqlStr2", "SqlPart2");
        ruleNameRepository.deleteAll();
    }

    @Test
    void testGetRuleNameList() throws Exception {
        ruleNameService.create(rule1);
        ruleNameService.create(rule2);

        MvcResult result = mockMvc.perform(get("/ruleName/list"))
            .andExpect(view().name("ruleName/list"))
            .andExpect(model().attributeExists("ruleNames"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddRuleNameForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/ruleName/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("ruleName/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateRuleName() throws Exception {
        List<RuleName> found = ruleNameRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/ruleName/validate")
            .param("name", rule1.getName())
            .param("description", rule1.getDescription())
            .param("json", rule1.getJson())
            .param("template", rule1.getTemplate())
            .param("sqlStr", rule1.getSqlStr())
            .param("sqlPart", rule1.getSqlPart())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/ruleName/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<RuleName> after = ruleNameRepository.findAll();
        assertEquals(1, after.size());
        RuleName ruleTest = after.get(0);
        assertEquals(rule1.getName(), ruleTest.getName());
        assertEquals(rule1.getDescription(), ruleTest.getDescription());
    }

    @Test
    void testValidateRuleNameErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/ruleName/validate")
            .param("name", "")
            .param("description", rule1.getDescription())
            .param("json", rule1.getJson())
            .param("template", rule1.getTemplate())
            .param("sqlStr", rule1.getSqlStr())
            .param("sqlPart", rule1.getSqlPart())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testShowUpdateForm() throws Exception {
        RuleName saved = ruleNameService.create(rule1);
        int ruleId = saved.getId();

        MvcResult result = mockMvc.perform(get("/ruleName/update/{id}", ruleId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("ruleName"))
            .andExpect(view().name("ruleName/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateRuleName() throws Exception {
        RuleName saved = ruleNameService.create(rule1);
        int ruleId = saved.getId();

        MvcResult result = mockMvc.perform(post("/ruleName/update/{id}", ruleId)
            .param("id", String.valueOf(ruleId))
            .param("name", saved.getName())
            .param("description", "UpdatedDesc")
            .param("json", saved.getJson())
            .param("template", saved.getTemplate())
            .param("sqlStr", saved.getSqlStr())
            .param("sqlPart", saved.getSqlPart())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/ruleName/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        RuleName updated = ruleNameService.findById(ruleId);
        assertEquals("UpdatedDesc", updated.getDescription());
    }

    @Test
    void testUpdateRuleNameErrorForm() throws Exception {
        RuleName saved = ruleNameService.create(rule1);
        int ruleId = saved.getId();

        MvcResult result = mockMvc.perform(post("/ruleName/update/{id}", ruleId)
            .param("id", String.valueOf(ruleId))
            .param("name", "")
            .param("description", saved.getDescription())
            .param("json", saved.getJson())
            .param("template", saved.getTemplate())
            .param("sqlStr", saved.getSqlStr())
            .param("sqlPart", saved.getSqlPart())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testDeleteRuleName() throws Exception {
        RuleName saved = ruleNameService.create(rule1);
        int ruleId = saved.getId();

        MvcResult result = mockMvc.perform(get("/ruleName/delete/{id}", ruleId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/ruleName/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<RuleName> after = ruleNameService.findAll();
        assertTrue(after.isEmpty());
    }
}
