package com.nnk.springboot;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CurvePointServiceTest {

    @Autowired
    private CurveService curveService;

    @Test
    public void testCreateAndFindCurvePoint() {
        CurvePoint curvePoint = new CurvePoint(10, new Timestamp(System.currentTimeMillis()), 10d, 30d);
        CurvePoint saved = curveService.create(curvePoint);
        Assert.assertNotNull(saved.getId());
        CurvePoint found = curveService.findById(saved.getId());
        Assert.assertEquals(Integer.valueOf(10), found.getCurveId());
        curveService.delete(saved.getId());
    }

    @Test
    public void testUpdateCurvePoint() {
        CurvePoint curvePoint = new CurvePoint(11, new Timestamp(System.currentTimeMillis()), 20d, 40d);
        CurvePoint saved = curveService.create(curvePoint);
        saved.setTerm(99d);
        CurvePoint updated = curveService.update(saved.getId(), saved);
        Assert.assertEquals(Double.valueOf(99d), updated.getTerm());
        curveService.delete(saved.getId());
    }

    @Test
    public void testDeleteCurvePoint() {
        CurvePoint curvePoint = new CurvePoint(12, new Timestamp(System.currentTimeMillis()), 50d, 60d);
        CurvePoint saved = curveService.create(curvePoint);
        Integer id = saved.getId();
        curveService.delete(id);
        try {
            curveService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("CurvePoint not fount"));
        }
    }

    @Test
    public void testFindAllCurvePoints() {
        int initialSize = curveService.findAll().size();
        CurvePoint curvePoint = new CurvePoint(13, new Timestamp(System.currentTimeMillis()), 70d, 80d);
        CurvePoint saved = curveService.create(curvePoint);
        List<CurvePoint> all = curveService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        curveService.delete(saved.getId());
    }
}
