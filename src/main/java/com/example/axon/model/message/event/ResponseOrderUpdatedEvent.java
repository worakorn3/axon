package com.example.axon.model.message.event;

import com.example.axon.model.message.OrderBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseOrderUpdatedEvent extends OrderBaseMessage {
}
