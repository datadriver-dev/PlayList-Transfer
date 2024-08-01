package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.YoutubeService;

@Controller
public class ConvertController {

    @Autowired
    YoutubeService youtubeService;

    @PostMapping("/convert")
    public String convertTracks(@RequestParam List<String> selectedTracks, @RequestParam String playlistName, Model model) {
        
        String playListId = youtubeService.createPlaylist(playlistName);

        for(String track : selectedTracks) {
            String trackId = youtubeService.getVideoID(track);
            // youtubeService.addTrack(trackId);
            System.out.println("Video Id : " + trackId);
        }
        String url = "https://www.youtube.com/playlist?list=" + playListId;
        model.addAttribute("url", url);
        return "convertResult";
    }
}
