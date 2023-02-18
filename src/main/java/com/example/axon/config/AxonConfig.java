package com.example.axon.config;

import com.example.axon.aggregate.OrderAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.DuplicateCommandHandlerResolver;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.AxonThreadFactory;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.tracing.SpanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

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
        EventProcessingConfigurer.PooledStreamingProcessorConfiguration psepConfig = (config, builder) -> builder.initialSegmentCount(32)
                .batchSize(100)
                .coordinatorExecutor(Executors.newScheduledThreadPool(1, new AxonThreadFactory("Axon-Coordinator[" + builder.name() + "]")))
                .workerExecutor(Executors.newScheduledThreadPool(100, new AxonThreadFactory("Axon-Worker[" + builder.name() + "]")))
                .tokenClaimInterval(5000)
                .claimExtensionThreshold(5000);

        return configurer -> configurer.eventProcessing(conf -> conf.usingPooledStreamingEventProcessors().registerPooledStreamingEventProcessorConfiguration(psepConfig));
    }

    @Qualifier("localSegment")
    @Bean
    public SimpleCommandBus commandBus(
            org.axonframework.config.Configuration configuration,
            DuplicateCommandHandlerResolver resolver,
            SpanFactory factory
    ) {
        SimpleCommandBus bus = SimpleCommandBus.builder()
                .duplicateCommandHandlerResolver(resolver)
                .messageMonitor(configuration.messageMonitor(CommandBus.class, "commandBus"))
                .spanFactory(factory)
                .build();

        return bus;
    }
}
