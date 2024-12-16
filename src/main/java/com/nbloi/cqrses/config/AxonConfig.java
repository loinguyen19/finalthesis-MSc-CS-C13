package com.nbloi.cqrses.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public CommandBus commandBus() {
        SimpleCommandBus commandBus = SimpleCommandBus.builder()
                .build();
        commandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());
        return commandBus;
    }

    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer configurer) {
        // Use tracking processors for asynchronous event handling
        configurer.usingTrackingEventProcessors();
    }
}

