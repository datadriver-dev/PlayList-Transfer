package com.example.demo.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String client_id;

    @Value("${spotify.client.secret}")
    private String client_secret;

    @Value("${spotify.redirect.uri}")
    private String redirect_uri;

    @Value("${spotify.scope}")
    private String scope;

    private String response_type = "code";
    private String code;
    private String access_token;
    
    public String getAccess_token() {
        return access_token;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

 public String getLoginUrl() {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                "&response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
    }

    String getTokenUrl() {
        return "https://accounts.spotify.com/api/token";
    }

    public void exchangeCodeForToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String auth = client_id + ":" + client_secret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getTokenUrl(), request, Map.class);

        // System.out.println(response);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                setAccess_token((String) responseBody.get("access_token"));
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getPlaylists(int limit, int offset) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(access_token);

        String url = "https://api.spotify.com/v1/me/playlists?limit=" + limit + "&offset=" + offset;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            System.out.println("Error: " + response.getStatusCode());
            return null;
        }
    }

    public Map<String, Object> getTrackList(String playlistId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(access_token);

        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "?fields=tracks(items(track(name)))";

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            System.out.println("Error: " + response.getStatusCode());
            return null;
        }
    }


}
