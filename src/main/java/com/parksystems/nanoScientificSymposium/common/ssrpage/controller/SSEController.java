package com.parksystems.nanoScientificSymposium.common.ssrpage.controller;

import com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage.LogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
public class SSEController {

    private final List<LogMessage> logMessageList = new CopyOnWriteArrayList<>();
    private final Flux<LogMessage> logMessageFlux;
    private FluxSink<LogMessage> logMessageSink;

    public SSEController() {
        this.logMessageFlux = Flux.<LogMessage>create(sink -> {
            this.logMessageSink = sink;
            this.logMessageList.forEach(logMessageSink::next);
        }).share(); // Ensures that all subscribers share the same sequence of events
    }

    @GetMapping(value = "/logMessages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<LogMessage>> streamLogMessages() {
        return logMessageFlux.map(logMessage -> ServerSentEvent.<LogMessage>builder().data(logMessage).build())
                .delayElements(Duration.ofSeconds(1)); // Delay elements to avoid overwhelming clients
    }

    @EventListener
    public void handleLogEvent(LogMessage logMessage) {
        logMessageList.add(logMessage);
        if (logMessageSink != null) {
            logMessageSink.next(logMessage);
        }
    }
}