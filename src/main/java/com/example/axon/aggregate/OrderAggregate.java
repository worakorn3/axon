package com.example.axon.aggregate;

import com.example.axon.model.message.command.OrderCommand;
import com.example.axon.model.message.command.OrderShipCommand;
import com.example.axon.model.message.event.OrderReceivedEvent;
import com.example.axon.model.message.event.OrderShippedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(cache = "axon-cache")
@NoArgsConstructor
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private boolean shipped = false;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    public void handle(OrderCommand command, EventGateway eventGateway) {
        log.info("Order received, generating orderId : {}", command.getOrderId());
        var event = new OrderReceivedEvent();
        event.setOrderId(command.getOrderId());
        apply(event);
    }

    @EventSourcingHandler
    public void on(OrderReceivedEvent event) {
        log.info("Order created {}", event.getOrderId());
        this.orderId = event.getOrderId();
    }

    @CommandHandler
    public void handle(OrderShipCommand command) {
        if (this.shipped) {
            log.warn("Order {} already out for shipping", command.getOrderId());
            return;
        }

        log.info("Messenger picked up order {}", command.getOrderId());
        var orderShippedEvent = new OrderShippedEvent();
        orderShippedEvent.setOrderId(command.getOrderId());
        apply(orderShippedEvent);
    }

    @EventSourcingHandler
    public void on(OrderShippedEvent event) {
        log.info("Order shipped {}", event.getOrderId());
        this.shipped = true;
    }

}
