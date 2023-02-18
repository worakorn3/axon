package com.example.axon.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;

    public void PaymentAggregate() {

    }
}
