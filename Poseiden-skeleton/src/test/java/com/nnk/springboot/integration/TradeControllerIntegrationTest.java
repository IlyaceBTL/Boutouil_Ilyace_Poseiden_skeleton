package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TradeControllerIntegrationTest {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private MockMvc mockMvc;

    Trade trade1;
    Trade trade2;

    @BeforeEach
    void setUp() {
        trade1 = new Trade();
        trade1.setAccount("Account1");
        trade1.setType("Type1");
        trade1.setBuyQuantity(100.0);
        trade2 = new Trade();
        trade2.setAccount("Account2");
        trade2.setType("Type2");
        trade2.setBuyQuantity(200.0);
        tradeRepository.deleteAll();
    }

    @Test
    void testGetTradeList() throws Exception {
        tradeService.save(trade1);
        tradeService.save(trade2);

        MvcResult result = mockMvc.perform(get("/trade/list"))
            .andExpect(view().name("trade/list"))
            .andExpect(model().attributeExists("trades"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddTradeForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/trade/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("trade/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateTrade() throws Exception {
        List<Trade> found = tradeRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/trade/validate")
            .param("account", trade1.getAccount())
            .param("type", trade1.getType())
            .param("buyQuantity", trade1.getBuyQuantity().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/trade/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<Trade> after = tradeRepository.findAll();
        assertEquals(1, after.size());
        Trade tradeTest = after.get(0);
        assertEquals(trade1.getAccount(), tradeTest.getAccount());
        assertEquals(trade1.getType(), tradeTest.getType());
        assertEquals(trade1.getBuyQuantity(), tradeTest.getBuyQuantity());
    }

    @Test
    void testValidateTradeErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/trade/validate")
            .param("account", "")
            .param("type", trade1.getType())
            .param("buyQuantity", trade1.getBuyQuantity().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    void testShowUpdateForm() throws Exception {
        Trade saved = tradeService.save(trade1);
        int tradeId = saved.getTradeId();

        MvcResult result = mockMvc.perform(get("/trade/update/{id}", tradeId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("trade"))
            .andExpect(view().name("trade/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateTrade() throws Exception {
        Trade saved = tradeService.save(trade1);
        int tradeId = saved.getTradeId();

        MvcResult result = mockMvc.perform(post("/trade/update/{id}", tradeId)
            .param("tradeId", String.valueOf(tradeId))
            .param("account", saved.getAccount())
            .param("type", saved.getType())
            .param("buyQuantity", "999.0")
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/trade/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        Trade updated = tradeService.findById(tradeId);
        assertEquals(999.0, updated.getBuyQuantity());
    }

    @Test
    void testUpdateTradeErrorForm() throws Exception {
        Trade saved = tradeService.save(trade1);
        int tradeId = saved.getTradeId();

        MvcResult result = mockMvc.perform(post("/trade/update/{id}", tradeId)
            .param("tradeId", String.valueOf(tradeId))
            .param("account", saved.getAccount())
            .param("type", saved.getType())
            .param("buyQuantity", "")
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    void testDeleteTrade() throws Exception {
        Trade saved = tradeService.save(trade1);
        int tradeId = saved.getTradeId();

        MvcResult result = mockMvc.perform(get("/trade/delete/{id}", tradeId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/trade/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<Trade> after = tradeService.findAll();
        assertTrue(after.isEmpty());
    }
}
