package com.example.axon.model.command;

import com.example.axon.model.OrderBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreateCompleteCommand extends OrderBaseMessage {
}
