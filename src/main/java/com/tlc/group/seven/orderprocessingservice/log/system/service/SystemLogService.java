package com.tlc.group.seven.orderprocessingservice.log.system.service;

import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.kafka.producer.KafkaProducer;
import com.tlc.group.seven.orderprocessingservice.log.system.model.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {

    @Autowired
    KafkaProducer kafkaProducer;

    public void sendSystemLogToReportingService(String title, String event, String description){
        SystemLog systemLog = new SystemLog(title, event, description, ServiceConstants.microserviceServiceName);
        kafkaProducer.sendResponseToKafkaLogData(systemLog);
    }
}
