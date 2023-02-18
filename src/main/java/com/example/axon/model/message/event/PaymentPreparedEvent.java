package com.example.axon.model.message.event;

import com.example.axon.model.message.PaymentBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentPreparedEvent extends PaymentBaseMessage {
}
