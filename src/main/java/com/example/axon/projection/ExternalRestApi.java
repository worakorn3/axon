package com.example.axon.projection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class ExternalRestApi {

    public Mono<Map<String, LocalDateTime>> callMessenger() {
        return Mono.just(Map.of("pickupDateTime", LocalDateTime.now().plusDays(1)));
    }
}
