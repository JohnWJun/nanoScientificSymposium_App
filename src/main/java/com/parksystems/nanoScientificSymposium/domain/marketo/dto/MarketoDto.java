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
        String salutation;
        String firstName;
        String lastName;
        String company;
        String department;
        String email;
        String phone;
        String researchTopic;


    }

    @Getter
    @Setter
    public static class MarketoImgForm{
       String imgCT_title;
       String imgCT_sample;
       String imgCT_desc;
       String imgCT_author;
       String imgCT_organization;
       String imgCT_email;
       String imgCT_system;
       String imgCT_mode;
       String imgCT_application;
       String imgCT_probe;
       String imgCT_scanSize;
       String imgCT_scanUnit;
       String imgCT_scanPoints;
       String imgCT_scanLines;
       String imgCT_citation;
       String imgCT_publicationLink;
       boolean psmktOptin;
       boolean reference_permission__c;
       boolean psOptin;


    }

    @Getter
    @Setter
    public static class MarketoListResponse{
        String requestId;
        int number;
        List<MarketoLead> result;
    }
}
