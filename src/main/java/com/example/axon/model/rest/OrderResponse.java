package com.example.axon.model.rest;

import com.example.axon.model.constant.OrderStatus;
import lombok.Data;

@Data
public class OrderResponse {

    private String orderId;
    private String paymentId;
    private OrderStatus status;
}
