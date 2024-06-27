package com.parksystems.nanoScientificSymposium.domain.marketo.mapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parksystems.nanoScientificSymposium.domain.marketo.dto.MarketoDto;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MarketoMapper {

    default MarketoDto.MarketoCheckInResponse toResponse(String result){
        if ( result == null ) {
            return null;
        }

        MarketoDto.MarketoCheckInResponse response = new MarketoDto.MarketoCheckInResponse();
        response.setResult(result);
        return response;
    }

    default MarketoDto.MarketoListResponse listToResponse(String result){
        if (result == null) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(result).getAsJsonObject();

        // Check if the "result" field is a JSON array
        if (!jsonResult.has("result") || !jsonResult.get("result").isJsonArray()) {
            // Handle the case where "result" is not a JSON array
            throw new IllegalStateException("Not a JSON Array: " + result);
        }

        JsonArray jsonArray = jsonResult.getAsJsonArray("result");

        List<MarketoDto.MarketoLead> resultList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject leadObject = element.getAsJsonObject();
            MarketoDto.MarketoLead lead = new MarketoDto.MarketoLead();
            lead.setId(leadObject.has("id") && !leadObject.get("id").isJsonNull() ? leadObject.get("id").getAsLong() : null);
            lead.setFirstName(leadObject.has("firstName") && !leadObject.get("firstName").isJsonNull() ? leadObject.get("firstName").getAsString() : null);
            lead.setLastName(leadObject.has("lastName") && !leadObject.get("lastName").isJsonNull() ? leadObject.get("lastName").getAsString() : null);
            lead.setEmail(leadObject.has("email") && !leadObject.get("email").isJsonNull() ? leadObject.get("email").getAsString() : null);
            lead.setSalutation(leadObject.has("salutation") && !leadObject.get("salutation").isJsonNull() ? leadObject.get("salutation").getAsString() : null);
            lead.setCompany(leadObject.has("company") && !leadObject.get("company").isJsonNull() ? leadObject.get("company").getAsString() : null);
            lead.setDepartment(leadObject.has("department") && !leadObject.get("department").isJsonNull() ? leadObject.get("department").getAsString() : null);
            lead.setPhone(leadObject.has("phone") && !leadObject.get("phone").isJsonNull() ? leadObject.get("phone").getAsString() : null);
            lead.setResearchTopic(leadObject.has("psResearchTopic") && !leadObject.get("psResearchTopic").isJsonNull() ? leadObject.get("psResearchTopic").getAsString() : null);
            resultList.add(lead);
        }

        MarketoDto.MarketoListResponse response = new MarketoDto.MarketoListResponse();
        response.setResult(resultList);
        response.setNumber(resultList.size());
        response.setRequestId(jsonResult.getAsJsonPrimitive("requestId").getAsString());
        return response;
    }

}
