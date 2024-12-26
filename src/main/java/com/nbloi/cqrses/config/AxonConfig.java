package com.nbloi.cqrses.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.DefaultCommandBusSpanFactory;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.disruptor.commandhandling.DisruptorCommandBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.monitoring.NoOpMessageMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public CommandBus commandBus() {
        SimpleCommandBus commandBus = SimpleCommandBus.builder()
                .messageMonitor(NoOpMessageMonitor.INSTANCE)
                .build();
        // customize thread pool
        commandBus.registerHandlerInterceptor(new BeanValidationInterceptor<>());

        commandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());
        return DisruptorCommandBus.builder()
                .bufferSize(4096)
                .build();
    }

    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer configurer) {
        // Use tracking processors for asynchronous event handling
        configurer.usingTrackingEventProcessors();
    }
}

