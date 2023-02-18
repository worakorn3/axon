package com.example.axon.model.event;

import com.example.axon.model.OrderBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderReceivedEvent extends OrderBaseMessage {
}
