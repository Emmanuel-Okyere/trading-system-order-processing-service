package com.tlc.group.seven.orderprocessingservice.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MarketData2 {
    public MarketData2(int MAX_PRICE_SHIFT, double LAST_TRADED_PRICE, double BID_PRICE, double SELL_LIMIT, double ASK_PRICE, int BUY_LIMIT, String TICKER) {
        this.MAX_PRICE_SHIFT = MAX_PRICE_SHIFT;
        this.LAST_TRADED_PRICE = LAST_TRADED_PRICE;
        this.BID_PRICE = BID_PRICE;
        this.SELL_LIMIT = SELL_LIMIT;
        this.ASK_PRICE = ASK_PRICE;
        this.BUY_LIMIT = BUY_LIMIT;
        this.TICKER = TICKER;
    }

    private int MAX_PRICE_SHIFT;
    private double LAST_TRADED_PRICE;
    private double BID_PRICE;
    private double SELL_LIMIT;
    private double ASK_PRICE;
    private int BUY_LIMIT;
    private String TICKER;
}
