package com.example.axon.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class PaymentBaseMessage implements BaseMessage {
    private String paymentId;

    @TargetAggregateIdentifier
    @Override
    public String getMessageId() {
        return this.paymentId;
    }
}
