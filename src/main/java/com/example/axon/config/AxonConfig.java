package com.example.axon.config;

import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean("axon-cache")
    public Cache axonCache() {
        return new WeakReferenceCache();
    }
}
