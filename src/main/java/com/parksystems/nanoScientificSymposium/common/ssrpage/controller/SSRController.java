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


    @GetMapping("/home/settings")
    public String showSSRPageSettings(Model model) {
        model.addAttribute("region", "");
        model.addAttribute("newRegistrantListId", 0L);
        model.addAttribute("newCheckInListId", 0L);
        return "settings";
    }

    @GetMapping("/home")
    public String showSSRPage(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "home";
    }

    @GetMapping("/home/Americas")
    public String showSSRPageUS(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-Americas";
    }

    @GetMapping("/home/Korea")
    public String showSSRPageKR(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-Korea";
    }

    @GetMapping("/home/China")
    public String showSSRPageCN(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-China";
    }

    @GetMapping("/home/Japan")
    public String showSSRPageJP(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-Japan";
    }

    @GetMapping("/home/Europe")
    public String showSSRPageEU(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-Europe";
    }

    @GetMapping("/home/SEAsia")
    public String showSSRPageSE(Model model) {

//        model.addAttribute("logMessages", logMessages);
        // Return the name of the Thymeleaf template to render
        return "region-SE Asia";
    }
//    @GetMapping("/logMessages")
//    public ResponseEntity
}
