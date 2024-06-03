package com.parksystems.nanoScientificSymposium.domain.marketo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parksystems.nanoScientificSymposium.domain.marketo.auth.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarketoService {

    private final Auth marketoAuth;
    @Value("${marketo.marketoInstance}")
    public String marketoInstance;
    static long USREListId= 2016;
    static long USCKListId= 2017;

    static long KRREListId= 2016;
    static long KRCKListId= 2017;

    public void changeListId( String region, long newREId, long newCKId){
        if (region.equals("US")){
            USREListId = newREId;
            USCKListId = newCKId;
        }
    }
    @Transactional
    public String addToList( String region, long userId){
        String result = null;
        String token = marketoAuth.getToken();
        long ckListId = 0L;
        long reListID = 0L;
        if (region.equals("US")) {
            ckListId = USCKListId;
            reListID = USREListId;
        } else if (region.equals("KR")) {

        }
        String endpoint1 = "https://988-FTP-549.mktorest.com/rest/v1/lists/" + ckListId + "/leads.json?access_token=" + token + "&id=" + userId;
        String endpoint2 = "https://988-FTP-549.mktorest.com/rest/v1/lists/" + reListID + "/leads.json?access_token=" + token + "&id=" + userId;
        boolean isMemberOfTheList = checkIsMember(endpoint2, userId);
        log.info(String.valueOf(isMemberOfTheList));
        if(isMemberOfTheList) {
            try {
                //Assemble the URL to retrieve data from
                URL url = new URL(endpoint1.toString());
                HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
                urlConn.setRequestProperty("accept", "text/json");
                urlConn.setDoOutput(true);
                int responseCode = urlConn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inStream = urlConn.getInputStream();
                    result = convertStreamToString(inStream);
                    log.info(result);
                } else {
                    result = "Status Code: " + responseCode;
                    log.info(result);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  throw new RuntimeException( "UserID: "+userId+" / Invalid eTicket, please check if you are registered in");

        return result;
    }
    public boolean checkIsMember(String endPoint, long id){
        boolean isMember = false;
        String result;
        try {
        URL url = new URL(endPoint.toString());
        HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
        urlConn.setRequestMethod("GET");
        urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
        urlConn.setRequestProperty("accept", "text/json");
        urlConn.setDoOutput(true);
        int responseCode = urlConn.getResponseCode();

        if (responseCode == 200){
            InputStream inStream = urlConn.getInputStream();
            String responseBody = convertStreamToString(inStream);
            // Parse JSON response
            JsonParser parser = new JsonParser();
            JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();


            log.info(String.valueOf(jsonResponse));
            isMember = isIdPresent(jsonResponse.toString(), id);

        }else{
            result = "Status Code: " + responseCode;
            isMember = false;
            throw new RuntimeException("Invalid eTicket, please check if you are registered in");
        }
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
            throw new RuntimeException("Invalid eTicket, please check if you are registered in");

        }


        return isMember;
    }


    private boolean isIdPresent(String responseJson, long idToCheck) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(responseJson);

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("result")) {
                JsonArray resultArray = jsonObject.getAsJsonArray("result");
                for (JsonElement element : resultArray) {
                    JsonObject item = element.getAsJsonObject();
                    if (item.has("id")) {
                        long id = item.get("id").getAsLong();
                        if (id == idToCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    @Transactional
    public String saveImage(byte[] file, String name,  String description, Boolean insertOnly){
        String result = null;
        String boundary =  "mktoBoundary" + String.valueOf(System.currentTimeMillis());
        String token = marketoAuth.getToken();
        JsonObject folder = new JsonObject();
        folder.addProperty("id", 5368); // Using addProperty instead of add
        folder.addProperty("type", "Folder");
        try {
            //Create the endpoint and then append all optional and required parameters
            StringBuilder endpoint = new StringBuilder("https://988-FTP-549.mktorest.com/rest/asset/v1/files.json?access_token=" + token);
            URL url = new URL(endpoint.toString());
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
            PrintWriter wr = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
            String base64EncodedFile = Base64.getEncoder().encodeToString(file);
            //Format and append the multipart data to the writer
            addMultipart(boundary, file, wr, "file", name, "image/png", urlConn );
            addMultipart(boundary, name, wr, "name");
            addMultipart(boundary, folder.toString(), wr, "folder");
            if (description != null){
                addMultipart(boundary, description, wr, "description");
            }
            if(insertOnly != null){
                addMultipart(boundary, insertOnly.toString(), wr, "insertOnly");
            }
            closeMultipart(boundary, wr);
            wr.flush();

            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200){
                InputStream inStream = urlConn.getInputStream();
                String responseBody = convertStreamToString(inStream);

                // Parse JSON response
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();

                // Extract the desired value by key
//                String SavedUrl = jsonResponse.get("url").getAsString();
// Check if the "result" array exists
                if (jsonResponse.has("result")) {
                    // Get the "result" array
                    JsonArray resultArray = jsonResponse.getAsJsonArray("result");

                    // Check if the array is not empty
                    if (resultArray.size() > 0) {
                        // Get the first element of the array
                        JsonObject resultObject = resultArray.get(0).getAsJsonObject();

                        // Check if the "url" field exists
                        if (resultObject.has("url")) {
                            // Get the URL value
                            String SavedUrl = resultObject.get("url").getAsString();
                            result = SavedUrl;
                        } else {
                            result = "URL not found in the JSON response.";
                        }
                    } else {
                        result = "No result found in the JSON response.";
                    }
                } else {
                    result = "Result array not found in the JSON response.";
                }
            }else{
                result = "Status Code: " + responseCode;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
    private void addMultipart(String boundary, byte[] requestBody, PrintWriter wr, String paramName, String name, String contentType, HttpsURLConnection urlConn) {


        wr.append("--" + boundary + "\r\n");
        wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + name + "\"\r\n");
        wr.append("Content-Type: " + contentType + "\r\n");
        wr.append("\r\n");
        wr.flush();
        try {

            urlConn.getOutputStream().write(requestBody);
            urlConn.getOutputStream().flush();
            wr.append("\r\n");
            wr.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addMultipart(String boundary, String requestBody, PrintWriter wr, String paramName) {
        wr.append("--" + boundary + "\r\n");
        wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n");
        wr.append("\r\n");
        wr.append(requestBody);
        wr.append("\r\n");
    }
    private void closeMultipart(String boundary, PrintWriter wr) {
        wr.append("--" + boundary);
    }


    private String readFile(String filePath){
        String fileOutPut = null;
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            char[] arr = new char[8 * 4096];
            StringBuilder buffer = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = br.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            fileOutPut = buffer.toString();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        return fileOutPut;
    }

    public String findList(String region, String type, String batchSize, String nextPageToken, String[] fields) {

        long id = 0L;
        if (region.equals("US") && type.equals("REGI")){
            id = USREListId;
        } else if (region.equals("US") && type.equals("CHECK")){
            id = USCKListId;
        }
        String token = marketoAuth.getToken();
        String data = null;
        try {
            //Assemble the URL to retrieve data from
            StringBuilder endpoint = new StringBuilder("https://988-FTP-549.mktorest.com/rest/v1/list/" + id + "/leads.json?access_token=" + token);
            //append optional params
            if (batchSize != null){
                endpoint.append("&batchSize=" + batchSize);
            }
            if (nextPageToken != null){
                endpoint.append("&nextPageToken=" + nextPageToken);
            }
            if (fields != null){
                endpoint.append("&fields=" + csvString(fields));
            }
            URL url = new URL(endpoint.toString());
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "text/json");
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                data = convertStreamToString(inStream);
            } else {
                data = "Status:" + responseCode;
            }
        } catch (MalformedURLException e) {
            System.out.println("URL not valid.");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }
    private String csvString(String[] fields) {
        StringBuilder fieldCsv = new StringBuilder();
        for (int i = 0; i < fields.length; i++){
            fieldCsv.append(fields[i]);
            if (i + 1 != fields.length){
                fieldCsv.append(",");
            }
        }
        return fieldCsv.toString();
    }

    private String convertStreamToString(InputStream inputStream) {
        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

}
