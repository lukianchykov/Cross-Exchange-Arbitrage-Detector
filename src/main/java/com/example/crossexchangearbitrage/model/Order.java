package com.example.crossexchangearbitrage.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Order {

    @EqualsAndHashCode.Include
    @NonNull
    private final String id;

    private final Double price;

    private final Double quantity;

    @NonNull
    private final Side side;

    public enum Side {
        BUY, SELL
    }

    public Order(@NonNull String id, Double price, Double quantity, @NonNull Side side) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
    }
}