package com.bank.enterprise.ui.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class BankApiClient {

    private static BankApiClient instance;
    private String jwtToken = null;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String API_BASE_URL = "http://localhost:8080/api/v1";

    private BankApiClient() {}

    public static BankApiClient getInstance() {
        if (instance == null){
            instance = new BankApiClient();
        }
        return instance;
    }

    public void logout(){
        this.jwtToken = null;
    }

    public boolean login(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(credentials)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root  = objectMapper.readTree(response.body());
            this.jwtToken = root.path("token").asText();
            return true;
        }
        return false;
    }

    // Add this inside BankApiClient.java
    public boolean openNewAccount(String accountType) throws Exception {
        if (jwtToken == null) throw new IllegalStateException("Not authenticated");

        // Creates the JSON: {"accountType": "SAVINGS"}
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("accountType", accountType);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/accounts"))
                .header("Authorization", "Bearer " + jwtToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Returns true if status is 200 OK or 201 Created
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    public JsonNode getMyAccounts() throws Exception{
        if (jwtToken == null) throw new IllegalStateException("Not Authenticated");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("/accounts"))
                .header("Authorization", "Bearer" + jwtToken).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }
}
