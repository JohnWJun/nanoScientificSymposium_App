package com.parksystems.nanoScientificSymposium.domain.qrCode.dto;

import lombok.Getter;
import lombok.Setter;

public class QRCodeDto {


    @Getter
    @Setter
    public static class QRResponse{
        String eTicket;
    }
}
