package com.parksystems.nanoScientificSymposium.common.ssrpage.controller;



import com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class SSRController {

    private final ApplicationContext applicationContext;


    @GetMapping("/home")
    public String showSSRPage(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "check-in-monitor";
    }
//    @GetMapping("/logMessages")
//    public ResponseEntity
}
