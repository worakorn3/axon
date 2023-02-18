package com.example.axon.model.command;

import com.example.axon.model.OrderPaymentBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePaymentCommand extends OrderPaymentBaseMessage {
}
