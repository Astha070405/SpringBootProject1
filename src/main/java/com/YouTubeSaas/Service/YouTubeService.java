package com.YouTubeSaas.Service;

import com.YouTubeSaas.Model.SearchVideo;
import com.YouTubeSaas.Model.Video;
import com.YouTubeSaas.Model.VideoDetails;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jdk.jshell.Snippet;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.tokens.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {


    private final WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base.url}")
    private String baseUrl;

    @Value("${youtube.api.max.related.videos}")
    private int maxRelatedVideos;

    public SearchVideo searchVideos(String videoTitle){
        List<String> videoIds = searchForVideoIds(videoTitle);
        if(videoIds.isEmpty()){
            return SearchVideo.builder()
                    .primaryVideo(null)
                    .relatedVideo(Collections.emptyList())
                    .build();
        }
        String primaryVideoId = videoIds.get(0);
        List<String> relatedVideoIds = videoIds.subList(1,Math.min(videoIds.size(),maxRelatedVideos));
        Video primaryVideo = getVideoById(primaryVideoId);
        List<Video>  relatedVideos = new ArrayList<>();
        for(String id:relatedVideoIds){
            Video video = getVideoById(id);

            if(video!=null){
                relatedVideos.add(video);
            }
        }

        return SearchVideo.builder()
                .primaryVideo(primaryVideo)
                .relatedVideo(relatedVideos)
                .build();
    }

    private List<String> searchForVideoIds(String videoTitle){
        SearchApiResponse response = webClientBuilder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part","snippet")
                        .queryParam("q",videoTitle)
                        .queryParam("type","video")
                        .queryParam("maxResults",maxRelatedVideos+1)
                        .queryParam("key",apiKey)
                        .build())
                .retrieve()
                .bodyToMono(SearchApiResponse.class)
                .block();
        if(response == null || response.items ==null){
            return Collections.emptyList();
        }
        List<String> videoIds = new ArrayList<>();
        for(SearchItem item:response.items){
            videoIds.add(item.id.videoId);
        }
        return videoIds;
    }
    public VideoDetails getVideoDetails(String videoId){
        VideoApiResponse response = webClientBuilder
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();
        if(response == null || response.items ==null){
            return null;
        }
        Snippet snippet = response.items.get(0).snippet;
        String thumbnailUrl = snippet.thumbnails.getBestThumbnailUrl();
        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.title)
                .description(snippet.description)
                .tags(snippet.tags == null ? Collections.emptyList() : snippet.tags)
                .thumbnailUrl(thumbnailUrl)
                .channelTitle(snippet.channelTitle)
                .publishedAt(snippet.pubishedAt)
                .build();


    }
    private Video getVideoById(String videoId){
        VideoApiResponse response = webClientBuilder
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();
        if(response == null || response.items ==null){
            return null;
        }
        Snippet snippet = response.items.get(0).snippet;
        return Video.builder()
                .id(videoId)
                .channelTitle(snippet.channelTitle)
                .title(snippet.title)
                .tags(snippet.tags == null ? Collections.emptyList():snippet.tags)
                .build();


    }
    @Data
    static class SearchApiResponse{
        List<SearchItem> items;
    }
    @Data
    static class SearchItem{
        Id id;
    }
    @Data
    static class Id{
        String videoId;
    }
    @Data
    static class VideoApiResponse{
        List<VideoItem> items;
    }
    @Data
    static class VideoItem{
        Snippet snippet;
    }
    @Data
    static class Snippet{
        String title;
        String description;
        String channelTitle;
        String pubishedAt;
        List<String> tags;
        Thumbnails thumbnails;
    }

    @Data
    static class Thumbnails {
        Thumbnail maxres;
        Thumbnail high;
        Thumbnail medium;
        Thumbnail _default;
        private String url;

        String getBestThumbnailUrl() {
            if (maxres != null) return maxres.url;
            if (high != null) return high.url;
            if (medium != null) return medium.url;
            return _default != null ? _default.url : "";

        }

    }

    @Data
    static class Thumbnail{
        String url;
    }
}



