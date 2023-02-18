package com.example.axon.controller;

import com.example.axon.model.rest.OrderResponse;
import com.example.axon.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/home")
@AllArgsConstructor
public class HomeController {

    private final OrderService orderService;

    @PostMapping("/order")
    public Mono<OrderResponse> order() {
        return orderService.order();
    }

    @GetMapping("/order/{orderId}")
    public Mono<OrderResponse> getOrder(
            @PathVariable String orderId
    ) {
        return orderService.getOrder(orderId);
    }
}
