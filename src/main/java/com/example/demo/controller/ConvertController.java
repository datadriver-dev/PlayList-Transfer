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
    private YoutubeService youtubeService;

    @PostMapping("/convert")
    public String convertTracks(@RequestParam List<String> selectedTracks, @RequestParam List<String> selectedArtists, @RequestParam String playlistName, Model model) {
        
        String playListId = youtubeService.createPlaylist(playlistName);

        for (int i = 0; i < selectedTracks.size(); i++) {
            String track = selectedTracks.get(i);
            String artist = selectedArtists.get(i);
            String trackId = youtubeService.getVideoID(artist + " " +track);
            youtubeService.addTrack(trackId, playListId);
        }

        String url = "https://www.youtube.com/playlist?list=" + playListId;
        model.addAttribute("url", url);
        return "convertResult";
    }
}
