package com.example.axon.config;

import com.example.axon.aggregate.OrderAggregate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.DuplicateCommandHandlerResolver;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.AxonThreadFactory;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.modelling.saga.repository.CachingSagaStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.axonframework.tracing.SpanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executors;

@Configuration
public class AxonConfig {

    @Bean("axon-cache")
    public Cache axonCache() {
        return new WeakReferenceCache();
    }

    @Primary
    @Bean("axon-xtream")
    public XStream xStream() {
        XStream xStream = new XStream();
        xStream.addPermission(AnyTypePermission.ANY);
        xStream.allowTypeHierarchy(Object.class);
        xStream.allowTypesByWildcard(new String[] { "com.example.axon.**" });
        return xStream;
    }

    @Bean("orderAggregateRepository")
    public EventSourcingRepository<OrderAggregate> orderAggregateRepository(
            EventStore eventStore,
            ParameterResolverFactory factory,
            Cache axonCache
    ) {
        return EventSourcingRepository.builder(OrderAggregate.class)
                .eventStore(eventStore)
                .parameterResolverFactory(factory)
                .cache(axonCache)
                .build();
    }

    @Bean
    public ConfigurerModule processorDefaultConfigurerModule() {
        EventProcessingConfigurer.PooledStreamingProcessorConfiguration psepConfig = (config, builder) -> builder.initialSegmentCount(8)
                .batchSize(50)
                .coordinatorExecutor(Executors.newScheduledThreadPool(1, new AxonThreadFactory("Axon-Coordinator[" + builder.name() + "]")))
                .workerExecutor(Executors.newScheduledThreadPool(8, new AxonThreadFactory("Axon-Worker[" + builder.name() + "]")))
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
        return SimpleCommandBus.builder()
                .duplicateCommandHandlerResolver(resolver)
                .messageMonitor(configuration.messageMonitor(CommandBus.class, "commandBus"))
                .spanFactory(factory)
                .build();
    }

    @Primary
    @Bean
    public Serializer jacksonSerializer() {
        var objectMapper = new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return JacksonSerializer.builder()
                .lenientDeserialization()
                .objectMapper(objectMapper)
                .build();
    }

    @Bean
    public Serializer xStreamSerializer() {
        XStream xStream = new XStream();
        xStream.allowTypeHierarchy(Object.class);
        return XStreamSerializer.builder()
                .lenientDeserialization()
                .disableAxonTypeSecurity()
                .xStream(xStream)
                .build();
    }

    @Lazy
    @Bean
    public SagaStore<Object> mySagaStore(
            EntityManagerProvider entityManagerProvider,
            @Qualifier("xStreamSerializer") Serializer serializer
            ) {
        return JpaSagaStore.builder()
                .entityManagerProvider(entityManagerProvider)
                .serializer(serializer)
                .build();
    }

    @Lazy
    @Primary
    @Bean
    public CachingSagaStore<Object> cachingSagaStore(
            SagaStore<Object> mySagaStore,
            Cache axonCache
    ) {
        return CachingSagaStore.builder()
                .delegateSagaStore(mySagaStore)
                .sagaCache(axonCache)
                .associationsCache(axonCache)
                .build();
    }
}
