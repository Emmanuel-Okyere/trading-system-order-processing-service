package com.tlc.group.seven.orderprocessingservice.kafka.consumer;

import com.tlc.group.seven.orderprocessingservice.log.system.SystemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private KafkaTemplate<String, SystemLog> kafkaTemplateLog;

    public String payload;
    @KafkaListener(topics = "market-data", groupId = "market-data-group")
    public void consumeFromKafka(String data){
        payload = data;
        LOGGER.info(String.format("Data received from Kafka -> %s", data));
    }

    public void sendResponseToKafkaSystemLog(SystemLog data){
        LOGGER.info(String.format("LogData:: Response sent to Kafka -> %s", data.toString()));
        Message<SystemLog> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "log-data")
                .build();
        kafkaTemplateLog.send(message);
    }
}