package com.example.axon.config;

import com.example.axon.aggregate.OrderAggregate;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean("axon-cache")
    public Cache axonCache() {
        return new WeakReferenceCache();
    }

    @Bean("orderAggregateRepository")
    public EventSourcingRepository<OrderAggregate> orderAggregateRepository(
            EventStore eventStore,
            ParameterResolverFactory factory
    ) {
        return EventSourcingRepository.builder(OrderAggregate.class)
                .eventStore(eventStore)
                .parameterResolverFactory(factory)
                .build();
    }

    @Bean
    public ConfigurerModule processorDefaultConfigurerModule() {
        EventProcessingConfigurer.PooledStreamingProcessorConfiguration psepConfig =
                (config, builder) -> builder.initialSegmentCount(32);

        return configurer -> configurer.eventProcessing(conf -> conf.registerPooledStreamingEventProcessorConfiguration(psepConfig));
    }

}
