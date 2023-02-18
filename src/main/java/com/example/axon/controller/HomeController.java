package com.example.axon.controller;

import com.example.axon.model.rest.OrderResponse;
import com.example.axon.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
