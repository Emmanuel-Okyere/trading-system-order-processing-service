package com.tlc.group.seven.orderprocessingservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    public String payload;

    @KafkaListener(topics = "market-data", groupId = "market-data-group")
    public void consumeFromKafka(List<MarketData2> data) {

        LOGGER.info(String.format("MarketData2:: received -> %s", data));

    }
}
