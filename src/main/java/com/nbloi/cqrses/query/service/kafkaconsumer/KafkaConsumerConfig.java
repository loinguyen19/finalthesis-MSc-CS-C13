package com.nbloi.cqrses.query.service.kafkaconsumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.util.backoff.FixedBackOff;;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        StringJsonMessageConverter converter = new StringJsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.addTrustedPackages("*"); // Configure trusted packages
        converter.setTypeMapper(typeMapper);

        factory.setConsumerFactory(consumerFactory);
        factory.setMessageConverter(converter); // Enable JSON conversion
        //TODO: check if by default, ConcurrentKafkaListenerContainerFactory has set for concurrency and Containers Properties??
//        factory.setConcurrency(3);
//        factory.getContainerProperties().setPollTimeout(3000);

        // Set error handler with retry and backoff
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new FixedBackOff(1000L, 3) // Retry every 1 second, up to 3 times
        ));
        return factory;
    }
}