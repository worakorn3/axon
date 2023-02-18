package com.example.axon.query;

import com.example.axon.aggregate.OrderAggregate;
import com.example.axon.model.message.event.ResponseOrderUpdatedEvent;
import com.example.axon.model.message.query.OrderQuery;
import com.example.axon.model.rest.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderQueryHandler {

    private final EventSourcingRepository<OrderAggregate> repository;

    public OrderQueryHandler(
            @Qualifier("orderAggregateRepository") EventSourcingRepository<OrderAggregate> repository
    ) {
        this.repository = repository;
    }

    @QueryHandler
    public OrderResponse on(OrderQuery query) {
        OrderAggregate aggregate = repository.load(query.getOrderId()).getWrappedAggregate().getAggregateRoot();

        var resp = new OrderResponse();
        resp.setOrderId(aggregate.getOrderId());
        resp.setPaymentId(null);
        resp.setStatus(aggregate.getOrderStatus());
        return resp;
    }

    @EventHandler
    public void on(ResponseOrderUpdatedEvent event, QueryUpdateEmitter emitter) {
        log.info("Emit update response");

        var response = new OrderResponse();
        response.setOrderId(event.getOrderId());
        response.setStatus(event.getOrderStatus());
        response.setPaymentId(null);
        emitter.emit(
                OrderQuery.class,
                q -> true,
                response
        );
    }
}
