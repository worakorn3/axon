package com.example.axon.aggregate;

import com.example.axon.model.constant.OrderStatus;
import com.example.axon.model.message.command.OrderCommand;
import com.example.axon.model.message.command.OrderShipCommand;
import com.example.axon.model.message.command.ResponseOrderUpdateCommand;
import com.example.axon.model.message.event.OrderReceivedEvent;
import com.example.axon.model.message.event.OrderShippedEvent;
import com.example.axon.model.message.event.ResponseOrderUpdatedEvent;
import com.example.axon.model.message.query.OrderQuery;
import com.example.axon.model.rest.OrderResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(cache = "axon-cache")
@NoArgsConstructor
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private boolean shipped = false;
    private OrderStatus orderStatus = OrderStatus.ORDER_PENDING;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    public void handle(OrderCommand command) {
        log.info("Order received, generating orderId : {}", command.getOrderId());
        var event = new OrderReceivedEvent();
        event.setOrderId(command.getOrderId());
        apply(event);
    }

    @EventSourcingHandler
    public void on(OrderReceivedEvent event) {
        log.info("Order created {}", event.getOrderId());
        this.orderId = event.getOrderId();
        this.orderStatus = OrderStatus.ORDER_RECEIVED;
    }

    @EventHandler
    public void onEvent(ResponseOrderUpdatedEvent event) {
        log.info("Emitting update to subscriber...");
    }

    @QueryHandler
    public OrderResponse on(OrderQuery query) {
        var resp = new OrderResponse();
        resp.setOrderId(this.orderId);
        resp.setPaymentId(null);
        resp.setStatus(this.orderStatus);
        return resp;
    }

    @CommandHandler
    public void handle(ResponseOrderUpdateCommand command) {
        var event = new ResponseOrderUpdatedEvent();
        event.setOrderId(command.getOrderId());
        apply(event);
    }

    @EventHandler
    public void on(ResponseOrderUpdatedEvent event, QueryUpdateEmitter emitter) {
        log.info("Emit update response");

        var response = new OrderResponse();
        response.setOrderId(event.getOrderId());
        response.setStatus(this.orderStatus);
        response.setPaymentId(null);
        emitter.emit(
                OrderResponse.class,
                q -> this.orderId.equals(event.getOrderId()),
                response
        );
    }

    /*
    * Used in Confirm payment flow
     */
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
        this.orderStatus = OrderStatus.ORDER_SHIPPED;
    }
}
