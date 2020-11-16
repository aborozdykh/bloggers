package com.alexcorp.bloggers.dto;

import lombok.Data;

@Data
public class YouTubeConnectionResultDto {

    private Integer channelSubscriberCount;
    private Integer channelVideosCount;
    private Integer channelViewsCount;

    private Integer minSubscriberCount;
    private Integer minVideosCount;
    private Integer minViewsCount;

    private Boolean allowed;

    public void setMinValues(int minSubscriberCount, int minViewsCount, int minVideosCount) {
        this.minSubscriberCount = minSubscriberCount;
        this.minViewsCount = minViewsCount;
        this.minVideosCount = minVideosCount;
    }

    public void setChannelValues(Integer subsCount, Integer viewsCount, Integer videosCount) {
        this.channelSubscriberCount = subsCount;
        this.channelViewsCount = viewsCount;
        this.channelVideosCount = videosCount;
    }
}
