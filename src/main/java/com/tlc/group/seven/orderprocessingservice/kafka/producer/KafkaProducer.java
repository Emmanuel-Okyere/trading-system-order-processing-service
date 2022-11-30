package com.tlc.group.seven.orderprocessingservice.kafka.producer;

import com.tlc.group.seven.orderprocessingservice.model.log.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaTemplate<String, LogData> kafkaTemplateLog;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaTemplate<String, LogData> kafkaTemplateLog) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateLog = kafkaTemplateLog;
    }

    public void sendResponseToKafkaMarketData(String data){
        LOGGER.info(String.format("Response sent to Kafka -> %s", data));
        Message<String> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "order-data")
                .build();
        kafkaTemplate.send(message);
    }

    public void sendResponseToKafkaLogData(LogData data){
        LOGGER.info(String.format("LogData:: Response sent to Kafka -> %s", data.toString()));
        Message<LogData> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "log-data")
                .build();
        kafkaTemplateLog.send(message);
    }
}
