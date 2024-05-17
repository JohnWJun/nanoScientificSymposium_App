package com.parksystems.nanoScientificSymposium.domain.marketo.controller;


import com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage.LogMessage;
import com.parksystems.nanoScientificSymposium.domain.marketo.dto.MarketoDto;
import com.parksystems.nanoScientificSymposium.domain.marketo.mapper.MarketoMapper;
import com.parksystems.nanoScientificSymposium.domain.marketo.service.MarketoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
@RestController
@RequestMapping("/marketo")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MarketoController {

    private final MarketoService service;
    private final MarketoMapper mapper;
    private final ApplicationEventPublisher publisher;

//    private final ApplicationContext applicationContext;

    @Transactional
    @PostMapping(value = "/check-in/{list-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addToList(@PathVariable("list-id") long listId,
                                    @RequestParam("id") String id) {
        long userId = Long.parseLong(id);
        try {
            long listId2 = 2016;

            String result = service.addToList(userId, listId, listId2);
            MarketoDto.MarketoCheckInResponse response = mapper.toResponse(result);

            // Log successful check-in
            log.info("User with ID {} checked into list {} successfully.", userId, listId);

            // Create a list of log messages
//            List<LogMessage> logMessages = applicationContext.getBean("logMessageList", List.class);
//            logMessages.add(new LogMessage(userId, listId, "success", null));
            publisher.publishEvent(new LogMessage(userId, listId, "success", null));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Log error if an exception occurs
            log.error("Error occurred while processing check-in request for user ID {}: {}", id, e.getMessage(), e);
//            List<LogMessage> logMessages = applicationContext.getBean("logMessageList", List.class);
//            logMessages.add(new LogMessage(userId, listId, "error", e.getMessage()));
            publisher.publishEvent(new LogMessage(userId, listId, "error", e.getMessage()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lists/{list-id}")
    public ResponseEntity getList(@PathVariable("list-id") long id){

        String result = service.findList(id,null,null,null);
        MarketoDto.MarketoListResponse response = mapper.listToResponse(result);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}


