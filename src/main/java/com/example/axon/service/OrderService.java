package com.example.axon.service;

import com.example.axon.model.message.command.OrderCommand;
import com.example.axon.model.message.event.ResponseOrderUpdatedEvent;
import com.example.axon.model.message.query.OrderQuery;
import com.example.axon.model.rest.OrderResponse;
import com.example.axon.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@ProcessingGroup("service-group")
public class OrderService {

    private CommandGateway commandGateway;
    private ReactorQueryGateway queryGateway;

    private ReactorCommandGateway reactorCommandGateway;

    public Mono<OrderResponse> order() {
        return StringUtil.generateUUID()
                .flatMap(uuid -> {
                    var orderCommand = new OrderCommand();
                    orderCommand.setOrderId(UUID.randomUUID().toString());

                    commandGateway.send(orderCommand);

                    return Mono.just(uuid);
                })
                .flatMap(uuid -> {
                    var query = new OrderQuery();
                        query.setOrderId(uuid);

                        return queryGateway.queryUpdates(query, ResponseTypes.instanceOf(OrderResponse.class)).next()
                                .subscribeOn(Schedulers.boundedElastic())
                                .doOnNext(n -> log.info("Next {}", n))
                                .doOnSuccess(s -> log.info("Success {}", s))
                                .doOnError(err -> log.error("Error", err));
                })
                .doOnError(err -> log.error("Error: ", err));
    }

    public Mono<OrderResponse> orderReactive() {
        return StringUtil.generateUUID()
                .flatMap(uuid -> {
                    var orderCommand = new OrderCommand();
                    orderCommand.setOrderId(UUID.randomUUID().toString());

                    return Mono.just(orderCommand);
                })
                .flatMap(cmd -> {
                    var query = new OrderQuery();
                    query.setOrderId(cmd.getOrderId());

                    return Mono.zip(
                            reactorCommandGateway.send(cmd)
                                    .switchIfEmpty(Mono.just("Done")),
                            queryGateway.queryUpdates(query, ResponseTypes.instanceOf(OrderResponse.class)).next()
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(n -> log.info("Next {}", n))
                                    .doOnSuccess(s -> log.info("Success {}", s))
                                    .doOnError(err -> log.error("Error", err))
                    )
                    .flatMap(tuple -> Mono.defer(() -> Mono.just(tuple.getT2())));
                })
                .doOnError(err -> log.error("Error: ", err));
    }

    public Mono<OrderResponse> getOrder(String orderId) {
        var query = new OrderQuery();
        query.setOrderId(orderId);

        return queryGateway.subscriptionQuery(query, ResponseTypes.instanceOf(OrderResponse.class)).next();
    }

    @EventHandler
    public void on(ResponseOrderUpdatedEvent event) {
        log.info("Some more logic to work {} {}", event.getOrderId(), event.getOrderStatus());
    }
}
