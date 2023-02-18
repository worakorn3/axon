package com.example.axon.model.message.command;

import com.example.axon.model.message.OrderBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseOrderUpdateCommand extends OrderBaseMessage {
}
