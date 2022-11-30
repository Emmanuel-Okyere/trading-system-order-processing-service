package com.tlc.group.seven.orderprocessingservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    public String payload;
    @KafkaListener(topics = "market-data", groupId = "market-data-group")
    public void consumeFromKafka(String data){
        payload = data;
        LOGGER.info(String.format("Data received from Kafka -> %s", data));
    }
}
