package com.YouTubeSaas.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SearchVideo {
    private Video primaryVideo;
    private List<Video> relatedVideo;

}
