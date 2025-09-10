package com.nnk.springboot;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
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
public class BidListServiceTest {

    @Autowired
    private BidListService bidListService;

    @Test
    public void testCreateAndFindBidList() {
        BidList bid = new BidList("Account Service", "Type Service", 15d);
        BidList saved = bidListService.create(bid);
        Assert.assertNotNull(saved.getBidListId());
        BidList found = bidListService.findById(saved.getBidListId());
        Assert.assertEquals("Account Service", found.getAccount());
        bidListService.delete(saved.getBidListId());
    }

    @Test
    public void testUpdateBidList() {
        BidList bid = new BidList("Account Up", "Type Up", 22d);
        BidList saved = bidListService.create(bid);
        saved.setBidQuantity(33d);
        BidList updated = bidListService.update(saved.getBidListId(), saved);
        Assert.assertEquals(Double.valueOf(33d), updated.getBidQuantity());
        bidListService.delete(saved.getBidListId());
    }

    @Test
    public void testDeleteBidList() {
        BidList bid = new BidList("Account Del", "Type Del", 44d);
        BidList saved = bidListService.create(bid);
        Integer id = saved.getBidListId();
        bidListService.delete(id);
        try {
            bidListService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("BidList not fount"));
        }
    }

    @Test
    public void testFindAllBidLists() {
        int initialSize = bidListService.findAll().size();
        BidList bid = new BidList("Account All", "Type All", 55d);
        BidList saved = bidListService.create(bid);
        List<BidList> all = bidListService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        bidListService.delete(saved.getBidListId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithIdThrowsException() {
        BidList bid = new BidList("Account", "Type", 10d);
        bid.setBidListId(999);
        bidListService.create(bid);
    }

    @Test
    public void testBidListSettersAndGetters() {
        BidList bid = new BidList();
        bid.setAskQuantity(1.1);
        bid.setBid(2.2);
        bid.setAsk(3.3);
        bid.setBenchmark("bench");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        bid.setBidListDate(now);
        bid.setCommentary("comment");
        bid.setSecurity("sec");
        bid.setStatus("status");
        bid.setTrader("trader");
        bid.setBook("book");
        bid.setCreationName("creator");
        bid.setCreationDate(now);
        bid.setRevisionName("revisor");
        bid.setRevisionDate(now);
        bid.setDealName("deal");
        bid.setDealType("type");
        bid.setSourceListId("source");
        bid.setSide("side");

        Assert.assertEquals(Double.valueOf(1.1), bid.getAskQuantity());
        Assert.assertEquals(Double.valueOf(2.2), bid.getBid());
        Assert.assertEquals(Double.valueOf(3.3), bid.getAsk());
        Assert.assertEquals("bench", bid.getBenchmark());
        Assert.assertEquals(now, bid.getBidListDate());
        Assert.assertEquals("comment", bid.getCommentary());
        Assert.assertEquals("sec", bid.getSecurity());
        Assert.assertEquals("status", bid.getStatus());
        Assert.assertEquals("trader", bid.getTrader());
        Assert.assertEquals("book", bid.getBook());
        Assert.assertEquals("creator", bid.getCreationName());
        Assert.assertEquals(now, bid.getCreationDate());
        Assert.assertEquals("revisor", bid.getRevisionName());
        Assert.assertEquals(now, bid.getRevisionDate());
        Assert.assertEquals("deal", bid.getDealName());
        Assert.assertEquals("type", bid.getDealType());
        Assert.assertEquals("source", bid.getSourceListId());
        Assert.assertEquals("side", bid.getSide());
    }
}
