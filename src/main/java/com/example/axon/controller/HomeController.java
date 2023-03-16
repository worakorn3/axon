package com.example.axon.controller;

import com.example.axon.model.constant.OrderStatus;
import com.example.axon.model.rest.OrderResponse;
import com.example.axon.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/home")
@AllArgsConstructor
@Slf4j
public class HomeController {

    private final OrderService orderService;

    @PostMapping("/v1/order/{waitingTime}")
    public Mono<OrderResponse> order(@PathVariable int waitingTime) {
        return orderService.order()
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(waitingTime), Mono.defer(() -> {
                    log.warn("Order not in time... send fallback instead");
                    OrderResponse fallback = new OrderResponse();
                    fallback.setStatus(OrderStatus.ORDER_PENDING);
                    return Mono.just(fallback);
                }))
                .doOnError(ex -> {
                    log.error("", ex);
                });
    }

    @PostMapping("/v2/order/{waitingTime}")
    public Mono<OrderResponse> orderReactive(@PathVariable int waitingTime) {
        return orderService.orderReactive()
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(waitingTime), Mono.defer(() -> {
                    log.warn("Order not in time... send fallback instead");
                    OrderResponse fallback = new OrderResponse();
                    fallback.setStatus(OrderStatus.ORDER_PENDING);
                    return Mono.just(fallback);
                }))
                .doOnError(ex -> {
                    log.error("", ex);
                });
    }

    @GetMapping("/order/{orderId}")
    public Mono<OrderResponse> getOrder(
            @PathVariable String orderId
    ) {
        return orderService.getOrder(orderId);
    }
}
