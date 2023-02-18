package com.example.axon.service;

import com.example.axon.model.message.command.OrderCommand;
import com.example.axon.model.rest.OrderResponse;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private CommandGateway commandGateway;
    private QueryGateway queryGateway;

    public Mono<OrderResponse> order() {
        var orderCommand = new OrderCommand();
        orderCommand.setOrderId(UUID.randomUUID().toString());

        commandGateway.send(orderCommand);

        return Mono.just(new OrderResponse());
    }
}
