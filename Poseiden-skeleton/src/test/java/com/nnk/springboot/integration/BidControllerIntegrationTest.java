package com.nnk.springboot.integration;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.BidListService;
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
class BidControllerIntegrationTest {

    @Autowired
    private BidListService bidListService;

    @Autowired
    private BidListRepository bidListRepository;

    @Autowired
    private MockMvc mockMvc;

    BidList bid1;
    BidList bid2;

    @BeforeEach
    void setUp() {
        bid1 = new BidList("Account test", "Type test", 10.00);
        bid2 = new BidList("Account test2", "Type test2", 20.00);
        bidListRepository.deleteAll();
    }

    @Test
    void testGetBidList() throws Exception {
        bidListService.save(bid1);
        bidListService.save(bid2);

        MvcResult result = mockMvc.perform(get("/bidList/list"))
            .andExpect(view().name("bidList/list"))
            .andExpect(model().attributeExists("bidLists"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddBidForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/bidList/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("bidList/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateBid() throws Exception {
        List<BidList> found = bidListRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/bidList/validate")
            .param("account", bid1.getAccount())
            .param("type", bid1.getType())
            .param("bidQuantity", bid1.getBidQuantity().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/bidList/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<BidList> after = bidListRepository.findAll();
        assertEquals(1, after.size());
        BidList bidTest = after.get(0);
        assertEquals(bid1.getAccount(), bidTest.getAccount());
        assertEquals(bid1.getType(), bidTest.getType());
        assertEquals(bid1.getBidQuantity(), bidTest.getBidQuantity());
    }

    @Test
    void testValidateBidErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/bidList/validate")
            .param("account", "")
            .param("type", bid1.getType())
            .param("bidQuantity", bid1.getBidQuantity().toString())
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("bidList/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<BidList> after = bidListRepository.findAll();
        assertTrue(after.isEmpty());
    }

    @Test
    void testShowUpdateForm() throws Exception {
        BidList saved = bidListService.save(bid1);
        int bidId = saved.getBidListId();

        MvcResult result = mockMvc.perform(get("/bidList/update/{id}", bidId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("bidList"))
            .andExpect(view().name("bidList/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateBid() throws Exception {
        BidList saved = bidListService.save(bid1);
        int bidId = saved.getBidListId();

        MvcResult result = mockMvc.perform(post("/bidList/update/{id}", bidId)
            .param("bidListId", String.valueOf(bidId))
            .param("type", saved.getType())
            .param("account", saved.getAccount())
            .param("bidQuantity", "200.00")
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/bidList/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        BidList updated = bidListService.findById(bidId);
        assertEquals(200.00, updated.getBidQuantity());
    }

    @Test
    void testUpdateBidErrorForm() throws Exception {
        BidList saved = bidListService.save(bid1);
        int bidId = saved.getBidListId();

        MvcResult result = mockMvc.perform(post("/bidList/update/{id}", bidId)
            .param("bidListId", String.valueOf(bidId))
            .param("type", saved.getType())
            .param("account", saved.getAccount())
            .param("bidQuantity", "")
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("bidList/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        BidList bidTest = bidListService.findById(bidId);
        assertEquals(10.00, bidTest.getBidQuantity());
    }

    @Test
    void testDeleteBid() throws Exception {
        BidList saved = bidListService.save(bid1);
        int bidId = saved.getBidListId();

        MvcResult result = mockMvc.perform(get("/bidList/delete/{id}", bidId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/bidList/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<BidList> after = bidListService.findAll();
        assertTrue(after.isEmpty());
    }
}
