package com.parksystems.nanoScientificSymposium.domain.marketo.controller;

import com.parksystems.nanoScientificSymposium.domain.marketo.auth.Auth;
import com.parksystems.nanoScientificSymposium.domain.marketo.service.MarketoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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


    @PostMapping("/check-in/{list-id}/{user-id}")
    public ResponseEntity addList(@PathVariable("user-id") long userId,
                                  @PathVariable("list-id") long listId
                                  ) {


        String result = service.addToList(userId, listId);
        System.out.println(result);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
