package com.tlc.group.seven.orderprocessingservice.log.system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class SystemLog {
    private String title;
    private String event;
    private String description;
    private String service;
}
