package com.example.demo.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YoutubeService {
    @Value("${youtube.client.id}")
    private String client_id;

    @Value("${youtube.redirect.uri}")
    private String redirect_uri;

    @Value("${youtube.response.type}")
    private String response_type;

    @Value("${youtube.scope}")
    private String scope;

    @Value("${youtube.client.secret}")
    private String client_secret;

    @Value("${youtube.grant.type}")
    private String grant_type;

    private String code;
    private String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                "&response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
    }

    public String getTokenUrl() {
        return "https://oauth2.googleapis.com/token";
    }

    public void exchangeCodeForToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("code", code);
        params.add("grant_type", grant_type);
        params.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getTokenUrl(), request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                setAccess_token((String) responseBody.get("access_token"));
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public String createPlaylist(String name) {

        String playlistId = null;
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> snippet = new HashMap<>();
        snippet.put("title", name);

        Map<String, Object> jsonInput = new HashMap<>();
        jsonInput.put("snippet", snippet);

        try {
            String requestBody = objectMapper.writeValueAsString(jsonInput);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://www.googleapis.com/youtube/v3/playlists?access_token=" + access_token
                    + "&part=id,snippet";

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();
            Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
            playlistId = (String) jsonResponse.get("id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return playlistId;
    }

    public String getVideoID(String query) {
        String videoId = null;
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

            String url = "https://www.googleapis.com/youtube/v3/search?access_token=" + access_token
                    + "&part=snippet&maxResults=1&q=" + encodedQuery + "&type=video";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) jsonResponse.get("items");

            if (items != null && !items.isEmpty()) {
                Map<String, Object> firstItem = items.get(0);
                Map<String, Object> id = (Map<String, Object>) firstItem.get("id");
                videoId = (String) id.get("videoId");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoId;
    }

    public void addTrack(String trackId, String playListId) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> resourceId = new HashMap<>();
        resourceId.put("kind", "youtube#video");
        resourceId.put("videoId", trackId);

        Map<String, Object> snippet = new HashMap<>();
        snippet.put("playlistId", playListId);
        snippet.put("resourceId", resourceId);

        Map<String, Object> jsonInput = new HashMap<>();
        jsonInput.put("snippet", snippet);

        try {
            String requestBody = objectMapper.writeValueAsString(jsonInput);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://www.googleapis.com/youtube/v3/playlistItems?access_token=" + access_token
                    + "&part=contentDetails,id,snippet,status";

            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
