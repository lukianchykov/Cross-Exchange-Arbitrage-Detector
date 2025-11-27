package com.example.crossexchangearbitrage.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;

@Getter
public class OrderBook {

    @NonNull
    private final String exchangeName;

    private final TreeMap<Double, List<Order>> bids;

    private final TreeMap<Double, List<Order>> asks;

    private final Map<String, Order> orderLookup;

    public OrderBook(@NonNull String exchangeName) {
        this.exchangeName = exchangeName;
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.asks = new TreeMap<>();
        this.orderLookup = new HashMap<>();
    }

    public void addOrder(@NonNull Order order) {
        if (orderLookup.containsKey(order.getId())) {
            throw new IllegalArgumentException("Order ID already exists: " + order.getId());
        }

        TreeMap<Double, List<Order>> targetMap = order.getSide() == Order.Side.BUY ? bids : asks;
        targetMap.computeIfAbsent(order.getPrice(), k -> new ArrayList<>()).add(order);
        orderLookup.put(order.getId(), order);
    }

    public boolean cancelOrder(@NonNull String orderId) {
        Order order = orderLookup.remove(orderId);
        if (order == null) {
            return false;
        }

        TreeMap<Double, List<Order>> targetMap = order.getSide() == Order.Side.BUY ? bids : asks;
        List<Order> ordersAtPrice = targetMap.get(order.getPrice());

        if (ordersAtPrice != null) {
            ordersAtPrice.remove(order);
            if (ordersAtPrice.isEmpty()) {
                targetMap.remove(order.getPrice());
            }
        }

        return true;
    }

    public Optional<Double> getBestBid() {
        Map.Entry<Double, List<Order>> entry = bids.firstEntry();
        return entry != null ? Optional.of(entry.getKey()) : Optional.empty();
    }

    public Optional<Double> getBestAsk() {
        Map.Entry<Double, List<Order>> entry = asks.firstEntry();
        return entry != null ? Optional.of(entry.getKey()) : Optional.empty();
    }

    public int size() {
        return orderLookup.size();
    }

    @Override
    public String toString() {
        return String.format("OrderBook{exchange='%s', bestBid=%s, bestAsk=%s, orders=%d}",
                exchangeName,
                getBestBid().map(b -> String.format("%.2f", b)).orElse("N/A"),
                getBestAsk().map(a -> String.format("%.2f", a)).orElse("N/A"),
                size());
    }
}