package com.YouTubeSaas.Controller;

import com.YouTubeSaas.Model.SearchVideo;
import com.YouTubeSaas.Service.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RequiredArgsConstructor
@Controller
@RequestMapping("/youtube")
public class YouTubeTagsController {
    private final YouTubeService YouTubeService;
    @Value("${youtube.api.key}")
    private String apiKey;
    private boolean isApiKeyConfigured(){
        return apiKey!=null && !apiKey.isEmpty();
    }
    @PostMapping("/search")
    public String videoTags(@RequestParam("videoTitle") String videoTitle , Model model){
        if(!isApiKeyConfigured()){  // Add ! (NOT)
            model.addAttribute("error","Api key is not configured");
            return "home";
        }
        if(videoTitle == null || videoTitle.isEmpty()){
            model.addAttribute("error","Youtube title required");
            return "home";
        }
        try{
            SearchVideo result = YouTubeService.searchVideos(videoTitle);
            model.addAttribute("primaryVideo",result.getPrimaryVideo());
            model.addAttribute("relatedVideos",result.getRelatedVideo());
            return "home";
        }catch(Exception e){
            model.addAttribute("error",e.getMessage());
            return "home";

        }

    }

}
