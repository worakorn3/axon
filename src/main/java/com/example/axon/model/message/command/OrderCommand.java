package com.example.axon.model.message.command;

import com.example.axon.model.message.OrderBaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderCommand extends OrderBaseMessage {
}
