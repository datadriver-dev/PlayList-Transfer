package com.example.demo.controller;

import com.example.demo.service.SpotifyService;
import com.example.demo.service.YoutubeService;

import io.micrometer.core.ipc.http.HttpSender.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private YoutubeService youtubeService;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/youtube_login")
    public String youtube_login() {
        return "redirect:" + youtubeService.getLoginUrl();
    }

    @GetMapping("/spotify_login")
    public String spotify_login() {
        return "redirect:" + spotifyService.getLoginUrl();
    }

    @GetMapping("/youtube_callback")
    public String youtube_callback(@RequestParam(required = false) String code) {
        if(code!=null) {
            youtubeService.setCode(code);
            youtubeService.exchangeCodeForToken();
        }
        System.out.println("Code :" + code);
        System.out.println("Token : " + youtubeService.getAccess_token());
        return "youtubelogged";
    }

    @GetMapping("/spotify_callback")
    public String spotify_callback(@RequestParam(required = false) String code) {
        if(code!=null) {
            spotifyService.setCode(code);
            spotifyService.exchangeCodeForToken();
            // System.out.println(spotifyService.getAccess_token());
        }
        return "youtubelogged";
    }
}
