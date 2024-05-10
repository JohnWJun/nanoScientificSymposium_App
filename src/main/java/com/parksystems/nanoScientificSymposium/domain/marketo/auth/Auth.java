package com.parksystems.nanoScientificSymposium.domain.marketo.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


@Component
public class Auth {
    //Replace marketoInstance, clientId, and clientSecret values
    @Value("${marketo.marketoInstance}")
    public String marketoInstance;
    @Value("${marketo.clientId}")
    public String clientId;
    @Value("${marketo.clientSecret}")
    public String clientSecret;
    JsonParser parser = new JsonParser();
    public String getToken() {
        String url = marketoInstance + "&client_id=" + clientId + "&client_secret=" + clientSecret;
        String result = getData(url);
        return result;
    }
    //Make request
    private String getData(String endpoint) {
        String token = null;
        try {
            URL url = new URL(endpoint);
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "application/json");
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                Reader reader = new InputStreamReader(inStream);
                JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
                token = jsonObject.get("access_token").getAsString();
            }else {
                throw new IOException("Status: " + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}


