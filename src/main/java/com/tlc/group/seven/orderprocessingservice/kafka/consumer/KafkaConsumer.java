package com.tlc.group.seven.orderprocessingservice.kafka.consumer;

import com.tlc.group.seven.orderprocessingservice.order.model.MarketData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    public List<MarketData> payload;
    @KafkaListener(topics = "market-data", groupId = "market-data-group")
    public void consumeFromKafka(List<MarketData> data){
        payload = data;

        LOGGER.info(String.format("Data received from Kafka -> %s", data));
        LOGGER.info(String.format("Data received from Kafka :: Class -> %s", data.getClass()));
    }
}
