package com.tlc.group.seven.orderprocessingservice.kafka.controller;

import com.tlc.group.seven.orderprocessingservice.kafka.producer.KafkaProducer;
import com.tlc.group.seven.orderprocessingservice.log.system.SystemLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestKafkaController {
    private KafkaProducer kafkaProducer;

    public TestKafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/api/v1/kafka/test")
    public ResponseEntity<String> testKafka(@RequestBody String data){
        kafkaProducer.sendResponseToKafkaMarketData(data);
        SystemLog logData = new SystemLog("auth-login-1", "click", "creating user account", "order-processing", new Date());
        kafkaProducer.sendResponseToKafkaLogData(logData);
        return ResponseEntity.ok("Data sent to Kafka...");
    }
}