package com.nnk.springboot.integration;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;

import com.nnk.springboot.services.CurveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Timestamp;
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
class CurveControllerIntegrationTest {

    @Autowired
    private CurveService curveService;

    @Autowired
    private CurvePointRepository curvePointRepository;

    @Autowired
    private MockMvc mockMvc;

    CurvePoint curve1;
    CurvePoint curve2;

    @BeforeEach
    void setUp() {
        curve1 = new CurvePoint(1,new Timestamp(System.currentTimeMillis()),10.0, 20.0);
        curve2 = new CurvePoint(2,new Timestamp(System.currentTimeMillis()),30.0, 40.0);
        curvePointRepository.deleteAll();
    }

    @Test
    void testGetCurveList() throws Exception {
        curveService.create(curve1);
        curveService.create(curve2);

        MvcResult result = mockMvc.perform(get("/curvePoint/list"))
            .andExpect(view().name("curvePoint/list"))
            .andExpect(model().attributeExists("curvePoints"))
            .andExpect(model().attributeExists("username"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testAddCurveForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/curvePoint/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("curvePoint/add"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testValidateCurve() throws Exception {
        List<CurvePoint> found = curvePointRepository.findAll();
        assertTrue(found.isEmpty());

        MvcResult result = mockMvc.perform(post("/curvePoint/validate")
            .param("curveId", String.valueOf(curve1.getCurveId()))
            .param("term", curve1.getTerm().toString())
            .param("value", curve1.getValue().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/curvePoint/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<CurvePoint> after = curvePointRepository.findAll();
        assertEquals(1, after.size());
        CurvePoint curveTest = after.get(0);
        assertEquals(curve1.getCurveId(), curveTest.getCurveId());
        assertEquals(curve1.getTerm(), curveTest.getTerm());
        assertEquals(curve1.getValue(), curveTest.getValue());
    }

    @Test
    void testValidateCurveErrorForm() throws Exception {
        MvcResult result = mockMvc.perform(post("/curvePoint/validate")
            .param("curveId", "")
            .param("term", curve1.getTerm().toString())
            .param("value", curve1.getValue().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());;
    }

    @Test
    void testShowUpdateForm() throws Exception {
        CurvePoint saved = curveService.create(curve1);
        int curveId = saved.getId();

        MvcResult result = mockMvc.perform(get("/curvePoint/update/{id}", curveId))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("curvePoint"))
            .andExpect(view().name("curvePoint/update"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testUpdateCurve() throws Exception {
        CurvePoint saved = curveService.create(curve1);
        int curveId = saved.getId();

        MvcResult result = mockMvc.perform(post("/curvePoint/update/{id}", curveId)
            .param("id", String.valueOf(curveId))
            .param("curveId", String.valueOf(saved.getCurveId()))
            .param("term", "100.0")
            .param("value", saved.getValue().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/curvePoint/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        CurvePoint updated = curveService.findById(curveId);
        assertEquals(100.0, updated.getTerm());
    }

    @Test
    void testUpdateCurveErrorForm() throws Exception {
        CurvePoint saved = curveService.create(curve1);
        int curveId = saved.getId();

        MvcResult result = mockMvc.perform(post("/curvePoint/update/{id}", curveId)
            .param("id", String.valueOf(curveId))
            .param("curveId", String.valueOf(saved.getCurveId()))
            .param("term", "")
            .param("value", saved.getValue().toString())
            .with(csrf()))
            .andExpect(status().isFound())
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        CurvePoint curveTest = curveService.findById(curveId);
        assertEquals(10.0, curveTest.getTerm());
    }

    @Test
    void testDeleteCurve() throws Exception {
        CurvePoint saved = curveService.create(curve1);
        int curveId = saved.getId();

        MvcResult result = mockMvc.perform(get("/curvePoint/delete/{id}", curveId))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/curvePoint/list"))
            .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<CurvePoint> after = curveService.findAll();
        assertTrue(after.isEmpty());
    }
}
