package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.query.service.EventReplayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/replay")
public class ReplayController {

    private final EventReplayService eventReplayService;

    public ReplayController(EventReplayService eventReplayService) {
        this.eventReplayService = eventReplayService;
    }

    @PostMapping("/{processorName}")
    public ResponseEntity<String> replay(@PathVariable String processorName) {
        eventReplayService.replayEvents(processorName); // replace processorName = the name of processor you want to replay
        return ResponseEntity.ok("Event replay started successfully for processor: 'orderProcessor'");
    }
}
