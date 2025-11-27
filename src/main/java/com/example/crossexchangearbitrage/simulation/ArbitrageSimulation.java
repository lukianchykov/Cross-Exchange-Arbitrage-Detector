package com.example.crossexchangearbitrage.simulation;

import com.example.crossexchangearbitrage.detector.ArbitrageDetector;
import com.example.crossexchangearbitrage.model.ArbitrageOpportunity;
import com.example.crossexchangearbitrage.model.Order;
import com.example.crossexchangearbitrage.model.OrderBook;

import java.util.Optional;

public class ArbitrageSimulation {

    public static void main(String[] args) {
        System.out.println("=== Cross-Exchange Arbitrage Detector Simulation ===\n");
        
        OrderBook binance = new OrderBook("Binance");
        OrderBook coinbase = new OrderBook("Coinbase");
        ArbitrageDetector detector = new ArbitrageDetector(binance, coinbase);

        System.out.println("--- Scenario 1: Initial Market State ---");
        binance.addOrder(new Order("B1", 50000.00, 1.0, Order.Side.BUY));
        binance.addOrder(new Order("B2", 49950.00, 0.5, Order.Side.BUY));
        binance.addOrder(new Order("B3", 50100.00, 1.0, Order.Side.SELL));
        binance.addOrder(new Order("B4", 50150.00, 0.8, Order.Side.SELL));

        coinbase.addOrder(new Order("C1", 49980.00, 1.2, Order.Side.BUY));
        coinbase.addOrder(new Order("C2", 49920.00, 0.7, Order.Side.BUY));
        coinbase.addOrder(new Order("C3", 50120.00, 0.9, Order.Side.SELL));
        coinbase.addOrder(new Order("C4", 50180.00, 1.1, Order.Side.SELL));

        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 2: Arbitrage Opportunity Appears ---");
        System.out.println("Action: Aggressive buyer on Binance pushes bid to 50200");
        binance.addOrder(new Order("B5", 50200.00, 2.0, Order.Side.BUY));
        
        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 3: Opportunity Magnitude Changes ---");
        System.out.println("Action: Another aggressive bid on Binance at 50250");
        binance.addOrder(new Order("B6", 50250.00, 1.5, Order.Side.BUY));
        
        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 4: Opportunity Disappears (Order Canceled) ---");
        System.out.println("Action: Top two bids on Binance canceled");
        binance.cancelOrder("B5");
        binance.cancelOrder("B6");
        
        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 5: Reverse Arbitrage Opportunity ---");
        System.out.println("Action: Low ask appears on Binance at 49900");
        binance.addOrder(new Order("B7", 49900.00, 1.0, Order.Side.SELL));
        
        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 6: Opportunity Disappears (Market Adjustment) ---");
        System.out.println("Action: Coinbase bid drops below Binance ask");
        coinbase.cancelOrder("C1");
        
        printState(binance, coinbase, detector);

        System.out.println("\n--- Scenario 7: Deep Order Book ---");
        System.out.println("Action: Adding multiple orders at various price levels");
        binance.addOrder(new Order("B8", 49800.00, 0.3, Order.Side.BUY));
        binance.addOrder(new Order("B9", 49750.00, 0.5, Order.Side.BUY));
        binance.addOrder(new Order("B10", 50300.00, 0.4, Order.Side.SELL));
        
        coinbase.addOrder(new Order("C5", 49850.00, 0.6, Order.Side.BUY));
        coinbase.addOrder(new Order("C6", 49800.00, 0.8, Order.Side.BUY));
        coinbase.addOrder(new Order("C7", 50250.00, 0.5, Order.Side.SELL));
        
        printState(binance, coinbase, detector);

        System.out.println("\n=== Simulation Complete ===");
    }

    private static void printState(OrderBook book1, OrderBook book2, ArbitrageDetector detector) {
        System.out.println("\nOrder Book State:");
        System.out.println("  " + book1);
        System.out.println("  " + book2);
        
        Optional<ArbitrageOpportunity> opportunity = detector.detectArbitrage();
        System.out.println("\nArbitrage Detection:");
        if (opportunity.isPresent()) {
            ArbitrageOpportunity opp = opportunity.get();
            System.out.println("  ✓ OPPORTUNITY FOUND!");
            System.out.println("    " + opp.toDetailedString());
            System.out.println("    Strategy: Buy at " + opp.getBuyExchange() + 
                             ", Sell at " + opp.getSellExchange());
        } else {
            System.out.println("  ✗ No arbitrage opportunity");
        }
        System.out.println("-".repeat(70));
    }
}