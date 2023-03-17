package com.example.axon.saga;

import com.example.axon.model.message.command.OrderShipCommand;
import com.example.axon.model.message.command.ResponseOrderUpdateCommand;
import com.example.axon.model.message.event.OrderReceivedEvent;
import com.example.axon.model.message.event.OrderShippedEvent;
import com.example.axon.projection.ExternalRestApi;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Saga(sagaStore = "cachingSagaStore")
@Slf4j
@ProcessingGroup("shop-saga-group")
@Getter
public class ShopSaga {

    @Autowired
    @JsonIgnore
    private transient CommandGateway commandGateway;

    @Autowired
    @JsonIgnore
    private transient ExternalRestApi externalRestApi;

    private boolean paid = false;
    private boolean shipped = false;

    private String orderId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderReceivedEvent event) {
        log.info("Starting saga... of orderId {}", event.getOrderId());
        SagaLifecycle.associateWith("orderId", event.getOrderId());

        this.orderId = event.getOrderId();

        externalRestApi.callMessenger().flatMap(pickDate -> {
            log.info("Pick up date received at {} / Order to be picked up by messenger at {}", LocalDateTime.now(), pickDate);
            // Response before let customer confirm order
            var orderShipCommand = new OrderShipCommand();
            orderShipCommand.setOrderId(this.orderId);
            commandGateway.send(orderShipCommand);

            var responseUpdateCommand = new ResponseOrderUpdateCommand();
            responseUpdateCommand.setOrderId(this.orderId);
            commandGateway.send(responseUpdateCommand);

            return Mono.just("Success");
        }).subscribe();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    @SneakyThrows
    public void on(OrderShippedEvent event) {
        if (paid) {
            log.warn("Order already paid, ending saga");
            SagaLifecycle.end();

            return;
        }

        log.info("Order {} out for shipping", event.getOrderId());
        this.shipped = true;

        Thread.sleep(1000);
    }
}
