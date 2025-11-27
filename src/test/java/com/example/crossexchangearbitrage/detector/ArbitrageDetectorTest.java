package com.example.crossexchangearbitrage.detector;

import com.example.crossexchangearbitrage.model.ArbitrageOpportunity;
import com.example.crossexchangearbitrage.model.Order;
import com.example.crossexchangearbitrage.model.OrderBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArbitrageDetectorTest {

    private OrderBook exchange1;

    private OrderBook exchange2;

    private ArbitrageDetector detector;

    @BeforeEach
    void setUp() {
        exchange1 = new OrderBook("Exchange1");
        exchange2 = new OrderBook("Exchange2");
        detector = new ArbitrageDetector(exchange1, exchange2);
    }

    @Test
    void testNoArbitrageWithEmptyBooks() {
        Optional<ArbitrageOpportunity> result = detector.detectArbitrage();
        assertFalse(result.isPresent());
    }

    @Test
    void testNoArbitrageWithNormalSpread() {
        exchange1.addOrder(new Order("1", 100.0, 1.0, Order.Side.BUY));
        exchange1.addOrder(new Order("2", 102.0, 1.0, Order.Side.SELL));
        exchange2.addOrder(new Order("3", 100.5, 1.0, Order.Side.BUY));
        exchange2.addOrder(new Order("4", 101.5, 1.0, Order.Side.SELL));

        Optional<ArbitrageOpportunity> result = detector.detectArbitrage();
        assertFalse(result.isPresent());
    }

    @Test
    void testArbitrageOpportunityExists() {
        exchange1.addOrder(new Order("1", 105.0, 1.0, Order.Side.BUY));
        exchange2.addOrder(new Order("2", 100.0, 1.0, Order.Side.SELL));

        Optional<ArbitrageOpportunity> result = detector.detectArbitrage();
        assertTrue(result.isPresent());

        ArbitrageOpportunity opp = result.get();
        assertEquals("Exchange2", opp.getBuyExchange());
        assertEquals(100.0, opp.getBuyPrice());
        assertEquals("Exchange1", opp.getSellExchange());
        assertEquals(105.0, opp.getSellPrice());
        assertEquals(5.0, opp.getProfitPerUnit());
    }

    @Test
    void testArbitrageOpportunityReverseDirection() {
        exchange1.addOrder(new Order("1", 100.0, 1.0, Order.Side.SELL));
        exchange2.addOrder(new Order("2", 105.0, 1.0, Order.Side.BUY));

        Optional<ArbitrageOpportunity> result = detector.detectArbitrage();
        assertTrue(result.isPresent());

        ArbitrageOpportunity opp = result.get();
        assertEquals("Exchange1", opp.getBuyExchange());
        assertEquals(100.0, opp.getBuyPrice());
        assertEquals("Exchange2", opp.getSellExchange());
        assertEquals(105.0, opp.getSellPrice());
    }

    @Test
    void testArbitrageDisappearsAfterCancellation() {
        exchange1.addOrder(new Order("1", 105.0, 1.0, Order.Side.BUY));
        exchange2.addOrder(new Order("2", 100.0, 1.0, Order.Side.SELL));

        assertTrue(detector.detectArbitrage().isPresent());

        exchange1.cancelOrder("1");
        assertFalse(detector.detectArbitrage().isPresent());
    }

    @Test
    void testOrderBookBestBidAsk() {
        exchange1.addOrder(new Order("1", 100.0, 1.0, Order.Side.BUY));
        exchange1.addOrder(new Order("2", 101.0, 1.0, Order.Side.BUY));
        exchange1.addOrder(new Order("3", 102.0, 1.0, Order.Side.SELL));
        exchange1.addOrder(new Order("4", 103.0, 1.0, Order.Side.SELL));

        assertEquals(101.0, exchange1.getBestBid().get());
        assertEquals(102.0, exchange1.getBestAsk().get());
    }

    @Test
    void testInvalidOrderThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                exchange1.addOrder(new Order("1", -100.0, 1.0, Order.Side.BUY))
        );
    }

    @Test
    void testDuplicateOrderIdThrowsException() {
        exchange1.addOrder(new Order("1", 100.0, 1.0, Order.Side.BUY));
        assertThrows(IllegalArgumentException.class, () ->
                exchange1.addOrder(new Order("1", 101.0, 1.0, Order.Side.BUY))
        );
    }

    @Test
    void testCancelNonExistentOrder() {
        assertFalse(exchange1.cancelOrder("nonexistent"));
    }
}