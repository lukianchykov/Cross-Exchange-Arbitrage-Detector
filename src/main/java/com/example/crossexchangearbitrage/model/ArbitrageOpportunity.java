package com.example.crossexchangearbitrage.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class ArbitrageOpportunity {

    @NonNull
    private final String buyExchange;
    
    private final Double buyPrice;
    
    @NonNull
    private final String sellExchange;
    
    private final Double sellPrice;
    
    private final Double profitPerUnit;

    public ArbitrageOpportunity(@NonNull String buyExchange, Double buyPrice,
                                @NonNull String sellExchange, Double sellPrice) {
        this.buyExchange = buyExchange;
        this.buyPrice = buyPrice;
        this.sellExchange = sellExchange;
        this.sellPrice = sellPrice;
        this.profitPerUnit = sellPrice - buyPrice;
    }

    public String toDetailedString() {
        return String.format(
            "ArbitrageOpportunity{buy from %s @ %.2f, sell to %s @ %.2f, profit=%.2f per unit}",
            buyExchange, buyPrice, sellExchange, sellPrice, profitPerUnit
        );
    }
}