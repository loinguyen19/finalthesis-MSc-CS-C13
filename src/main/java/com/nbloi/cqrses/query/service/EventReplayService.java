package com.nbloi.cqrses.query.service;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public class EventReplayService {

    private final EventProcessingConfiguration eventProcessingConfiguration;

    public EventReplayService(EventProcessingConfiguration eventProcessingConfiguration) {
        this.eventProcessingConfiguration = eventProcessingConfiguration;
    }

    public void replayEvents(String processorName) {
        eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class)
                .ifPresent(trackingEventProcessor -> {
                    trackingEventProcessor.shutDown();
                    trackingEventProcessor.resetTokens();
                    trackingEventProcessor.start();
                });
    }
}

