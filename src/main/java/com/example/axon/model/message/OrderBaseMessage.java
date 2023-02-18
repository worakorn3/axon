package com.example.axon.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class OrderBaseMessage implements BaseMessage {
    private String orderId;

    @Override
    @TargetAggregateIdentifier
    public String getMessageId() {
        return this.orderId;
    }
}
