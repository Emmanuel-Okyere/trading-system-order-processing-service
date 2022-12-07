package com.tlc.group.seven.orderprocessingservice.portfolio.payload;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TopUpRequest {
    @NotNull
    private Double amountToTopUp;
}
