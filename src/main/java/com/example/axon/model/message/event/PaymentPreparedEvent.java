package com.example.axon.model.event;

import com.example.axon.model.PaymentBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentPreparedEvent extends PaymentBaseMessage {
}
