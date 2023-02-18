package com.example.axon.model.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class OrderBaseMessage implements BaseMessage {
    @TargetAggregateIdentifier
    private String orderId;

    @Override
    public String getMessageId() {
        return this.orderId;
    }
}
