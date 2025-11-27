package com.example.crossexchangearbitrage.detector;

import com.example.crossexchangearbitrage.model.ArbitrageOpportunity;
import com.example.crossexchangearbitrage.model.OrderBook;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class ArbitrageDetector {

    @NonNull
    private final OrderBook orderBook1;
    
    @NonNull
    private final OrderBook orderBook2;

    public ArbitrageDetector(@NonNull OrderBook orderBook1, @NonNull OrderBook orderBook2) {
        if (orderBook1.getExchangeName().equals(orderBook2.getExchangeName())) {
            throw new IllegalArgumentException(
                "Order books must represent different exchanges");
        }
        
        this.orderBook1 = orderBook1;
        this.orderBook2 = orderBook2;
    }

    public Optional<ArbitrageOpportunity> detectArbitrage() {
        Optional<Double> bid1 = orderBook1.getBestBid();
        Optional<Double> ask1 = orderBook1.getBestAsk();
        Optional<Double> bid2 = orderBook2.getBestBid();
        Optional<Double> ask2 = orderBook2.getBestAsk();

        if (bid1.isPresent() && ask2.isPresent() && bid1.get() > ask2.get()) {
            return Optional.of(new ArbitrageOpportunity(
                orderBook2.getExchangeName(), ask2.get(),
                orderBook1.getExchangeName(), bid1.get()
            ));
        }

        if (bid2.isPresent() && ask1.isPresent() && bid2.get() > ask1.get()) {
            return Optional.of(new ArbitrageOpportunity(
                orderBook1.getExchangeName(), ask1.get(),
                orderBook2.getExchangeName(), bid2.get()
            ));
        }

        return Optional.empty();
    }
}