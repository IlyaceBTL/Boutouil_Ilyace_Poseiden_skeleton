package com.nnk.springboot;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeServiceTest {

    @Autowired
    private TradeService tradeService;

    @Test
    public void testCreateAndFindTrade() {
        Trade trade = new Trade("Account Service", "Type Service");
        Trade saved = tradeService.save(trade);
        Assert.assertNotNull(saved.getTradeId());
        Trade found = tradeService.findById(saved.getTradeId());
        Assert.assertEquals("Account Service", found.getAccount());
        tradeService.delete(saved.getTradeId());
    }

    @Test
    public void testUpdateTrade() {
        Trade trade = new Trade("Account Up", "Type Up");
        Trade saved = tradeService.save(trade);
        saved.setAccount("Account Updated");
        Trade updated = tradeService.update(saved.getTradeId(), saved);
        Assert.assertEquals("Account Updated", updated.getAccount());
        tradeService.delete(saved.getTradeId());
    }

    @Test
    public void testDeleteTrade() {
        Trade trade = new Trade("Account Del", "Type Del");
        Trade saved = tradeService.save(trade);
        Integer id = saved.getTradeId();
        tradeService.delete(id);
        try {
            tradeService.findById(id);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Trade not fount"));
        }
    }

    @Test
    public void testFindAllTrades() {
        int initialSize = tradeService.findAll().size();
        Trade trade = new Trade("Account All", "Type All");
        Trade saved = tradeService.save(trade);
        List<Trade> all = tradeService.findAll();
        Assert.assertTrue(all.size() >= initialSize + 1);
        tradeService.delete(saved.getTradeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdThrowsException() {
        tradeService.findById(-1);
    }

    @Test
    public void testTradeSettersAndGetters() {
        Trade trade = new Trade();
        trade.setTradeId(123);
        trade.setBuyQuantity(10.5);
        trade.setSellQuantity(5.5);
        trade.setBuyPrice(100.0);
        trade.setSellPrice(105.0);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        trade.setTradeDate(now);
        trade.setSecurity("SECURITY");
        trade.setStatus("STATUS");
        trade.setTrader("TRADER");
        trade.setBenchmark("BENCHMARK");
        trade.setBook("BOOK");
        trade.setCreationName("CREATOR");
        trade.setRevisionName("REVISION");
        trade.setRevisionDate(now);
        trade.setDealName("DEAL");
        trade.setDealType("TYPE");
        trade.setSourceListId("SOURCE");
        trade.setSide("SIDE");

        Assert.assertEquals(Integer.valueOf(123), trade.getTradeId());
        Assert.assertEquals(Double.valueOf(10.5), trade.getBuyQuantity());
        Assert.assertEquals(Double.valueOf(5.5), trade.getSellQuantity());
        Assert.assertEquals(Double.valueOf(100.0), trade.getBuyPrice());
        Assert.assertEquals(Double.valueOf(105.0), trade.getSellPrice());
        Assert.assertEquals(now, trade.getTradeDate());
        Assert.assertEquals("SECURITY", trade.getSecurity());
        Assert.assertEquals("STATUS", trade.getStatus());
        Assert.assertEquals("TRADER", trade.getTrader());
        Assert.assertEquals("BENCHMARK", trade.getBenchmark());
        Assert.assertEquals("BOOK", trade.getBook());
        Assert.assertEquals("CREATOR", trade.getCreationName());
        Assert.assertEquals("REVISION", trade.getRevisionName());
        Assert.assertEquals(now, trade.getRevisionDate());
        Assert.assertEquals("DEAL", trade.getDealName());
        Assert.assertEquals("TYPE", trade.getDealType());
        Assert.assertEquals("SOURCE", trade.getSourceListId());
        Assert.assertEquals("SIDE", trade.getSide());
    }
}
