package com.tlc.group.seven.orderprocessingservice.order.payload;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
@Data
@Builder
@Jacksonized
public class ErrorResponse {
    private String status;
    private String message;
}
