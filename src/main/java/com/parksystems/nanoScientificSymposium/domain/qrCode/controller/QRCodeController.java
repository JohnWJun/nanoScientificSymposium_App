package com.parksystems.nanoScientificSymposium.domain.qrCode.controller;

import com.google.zxing.WriterException;
import com.parksystems.nanoScientificSymposium.domain.marketo.service.MarketoService;
import com.parksystems.nanoScientificSymposium.domain.qrCode.dto.QRCodeDto;
import com.parksystems.nanoScientificSymposium.domain.qrCode.mapper.QRCodeMapper;
import com.parksystems.nanoScientificSymposium.domain.qrCode.service.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/qr-code")
@Validated
@Slf4j
@RequiredArgsConstructor
public class QRCodeController {

    private final MarketoService marketoService;
    private final QRCodeMapper qrCodeMapper;
    @GetMapping(value = "/generate")
    @ResponseBody
    public ResponseEntity generateQRCode(@RequestParam("text") String id,
                                         @RequestParam("width") int width,
                                         @RequestParam("height") int height) throws IOException, WriterException {

        System.out.println(id);
        byte[] qrCode = QRCodeGenerator.generateQRCodeImage(id, width, height);

        String savedURL = marketoService.saveImage(qrCode,id+"eTicket.png","qr code image", true);
        QRCodeDto.QRResponse response = qrCodeMapper.ToResponse(savedURL);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
