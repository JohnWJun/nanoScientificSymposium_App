package com.parksystems.nanoScientificSymposium.domain.marketo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MarketoDto {
    @Getter
    @Setter
    public static class MarketoCheckIn{
        long id;
    }

    @Getter
    @Setter
    public static class MarketoCheckInResponse{
        String result;
    }
    @Getter
    @Setter
    public static class MarketoLead{
        long id;
        String firstName;
        String lastName;
        String email;

    }

    @Getter
    @Setter
    public static class MarketoListResponse{
        String requestId;
        List<MarketoLead> result;
    }
}
