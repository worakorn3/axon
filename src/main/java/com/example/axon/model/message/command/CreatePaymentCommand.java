package com.example.axon.model.message.command;

import com.example.axon.model.message.OrderPaymentBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePaymentCommand extends OrderPaymentBaseMessage {
}
