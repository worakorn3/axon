package com.example.axon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    public static Mono<String> generateUUID() {
        return Mono.defer(() -> Mono.just(UUID.randomUUID().toString())).subscribeOn(Schedulers.immediate());
    }
}
