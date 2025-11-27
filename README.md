# Cross-Exchange Arbitrage Detector

A Java-based system for monitoring order books from two exchanges and detecting arbitrage opportunities in real-time.

## Overview

This system identifies risk-free profit opportunities when the best bid price on one exchange exceeds the best ask price on another exchange, allowing simultaneous buy/sell operations for guaranteed profit.

## Architecture & Design

### Core Components

#### 1. **Order** (`Order.java`)
- Immutable value object representing a single order
- Contains: ID, price, quantity, and side (BUY/SELL)
- Input validation ensures data integrity
- Follows Value Object pattern

#### 2. **OrderBook** (`OrderBook.java`)
- Maintains buy and sell orders for a single exchange
- Provides efficient best bid/ask retrieval

**Data Structure Rationale:**

- **TreeMap for Bids** (Descending order)
    - **Why:** O(1) access to best bid via `firstEntry()`
    - **Trade-off:** O(log n) insert/delete vs O(n) for PriorityQueue deletion

- **TreeMap for Asks** (Natural order)
    - **Why:** O(1) access to best ask via `firstEntry()`
    - **Benefit:** Sorted by price automatically

- **HashMap for Order Lookup**
    - **Why:** O(1) average-case lookup for cancellation by ID
    - **Alternative considered:** Linear search in TreeMap - rejected due to O(n) complexity

#### 3. **ArbitrageDetector** (`ArbitrageDetector.java`)
- Monitors two order books for arbitrage opportunities
- Immutable after construction
- O(1) detection time complexity

**Detection Logic:**
```
Opportunity exists if:
  bestBid(Exchange1) > bestAsk(Exchange2) OR
  bestBid(Exchange2) > bestAsk(Exchange1)
```

#### 4. **ArbitrageOpportunity** (`ArbitrageOpportunity.java`)
- Immutable result object
- Contains: buy/sell exchanges, prices, and profit calculation

## Running the Simulation

### Prerequisites
- Java 11 or higher
- Maven or Gradle (optional, for dependencies)

### Expected Output

The simulation demonstrates:

1. **Initial State** - Normal market with no arbitrage
2. **Opportunity Appears** - Aggressive bid creates profitable gap
3. **Magnitude Changes** - Profit potential increases
4. **Opportunity Disappears** - Order cancellation closes the gap
5. **Reverse Direction** - Arbitrage in opposite direction
6. **Market Adjustment** - Natural market forces eliminate opportunity
7. **Deep Order Book** - Multiple price levels

## Testing

```bash
# Run unit tests (requires JUnit 5)
mvn test
```

Tests cover:
- Empty order books
- Normal market spreads
- Arbitrage detection (both directions)
- Order cancellation
- Data validation
- Edge cases
