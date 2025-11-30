package com.YouTubeSaas.Controller;

import com.YouTubeSaas.Model.VideoDetails;
import com.YouTubeSaas.Service.ThumbnailService;
import com.YouTubeSaas.Service.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class YouTubeVideoController {
    private final YouTubeService youTubeService;
    private final ThumbnailService service;
    @GetMapping("/youtube/video-details")
    public String showVideoForm(){
        return "video-details";
    }
    @PostMapping("/youtube/video-details")
    public String fetchVideoDetails(@RequestParam String videoUrlOrId , Model model){
        String videoId = service.extractVideoId(videoUrlOrId);
         if(videoId == null){
             model.addAttribute("error","Invalid youtube url or id");
             return "video-details";
         }
        VideoDetails details = youTubeService.getVideoDetails(videoId);
        if(details == null){
            model.addAttribute("error","Video not found ");

        }
        else{
            model.addAttribute("videoDetails",details);
        }
        model.addAttribute("videoUrlOrId",videoUrlOrId);
        return "video-details";
    }

}
