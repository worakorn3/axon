package com.example.axon.model.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class OrderPaymentBaseMessage implements BaseMessage {
    private String paymentId;
    private String orderId;


    @Override
    @TargetAggregateIdentifier
    public String getMessageId() {
        return this.paymentId + "#" + this.orderId;
    }
}
