package com.parksystems.nanoScientificSymposium.domain.marketo.controller;


import com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage.LogMessage;
import com.parksystems.nanoScientificSymposium.domain.marketo.dto.MarketoDto;
import com.parksystems.nanoScientificSymposium.domain.marketo.mapper.MarketoMapper;
import com.parksystems.nanoScientificSymposium.domain.marketo.service.MarketoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/marketo")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MarketoController {

    private final MarketoService service;
    private final MarketoMapper mapper;
    private final ApplicationEventPublisher publisher;


    @Transactional
    @PostMapping(value = "/check-in/{region}/{list-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addToListUS(@PathVariable("list-id") long listId,
                                    @PathVariable("region") String region,
                                    @RequestParam("id") String id) {
        long userId = Long.parseLong(id);
        try {
//            long listId2 = 2016;

            String result = service.addToList(region, userId);
            MarketoDto.MarketoCheckInResponse response = mapper.toResponse(result);

            // Log successful check-in
            log.info("User with ID {} checked into list {} successfully.", userId, listId);


            publisher.publishEvent(new LogMessage("Americas",userId, listId, "success", null));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Log error if an exception occurs
            log.error("Error occurred while processing check-in request for user ID {}: {}", id, e.getMessage(), e);
            publisher.publishEvent(new LogMessage("Americas",userId, listId, "error", e.getMessage()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lists/{region}/{list-type}")
    public ResponseEntity getList(@PathVariable("region") String region,
                                  @PathVariable("list-type") String type){

        String result = service.findList(region,type,null,null,null);
        MarketoDto.MarketoListResponse response = mapper.listToResponse(result);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("/lists/{region}/{Register-list-id}/{CheckIn-list-id}")
    public ResponseEntity patchListId(@PathVariable("Register-list-id") long REid,
                                      @PathVariable("CheckIn-list-id") long CKid,
                                      @PathVariable("region") String region
                                      ){
        service.changeListId(region,REid,CKid);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}


