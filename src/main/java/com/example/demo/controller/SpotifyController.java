package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.SpotifyService;

@Controller
public class SpotifyController {
    
    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/spotify_playlists")
    public ResponseEntity<Map<String, Object>> getPlaylists(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Map<String, Object> playlists = spotifyService.getPlaylists(limit, offset);

        Object items = playlists.get("items");
        System.out.println();

        

        return ResponseEntity.ok(playlists);
    }
}
