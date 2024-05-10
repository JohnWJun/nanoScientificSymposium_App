package com.parksystems.nanoScientificSymposium.domain.qrCode.mapper;

import com.parksystems.nanoScientificSymposium.domain.qrCode.dto.QRCodeDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface QRCodeMapper {
    default QRCodeDto.QRResponse ToResponse(String eTicket){
        if ( eTicket == null ) {
            return null;
        }

        QRCodeDto.QRResponse qRResponse = new QRCodeDto.QRResponse();
        qRResponse.setETicket(eTicket);
        return qRResponse;
    };
}
