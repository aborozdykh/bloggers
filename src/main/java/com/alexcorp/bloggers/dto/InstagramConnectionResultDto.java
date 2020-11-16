package com.alexcorp.bloggers.dto;

import lombok.Data;

@Data
public class InstagramConnectionResultDto {

    private Integer accFollowersCount;
    private Integer accMediaCount;

    private Integer minFollowersCount;
    private Integer minMediaCount;

    private Boolean allowed;

    public void setMinValues(int minFollowersCount, int minMediaCount) {
        this.minFollowersCount = minFollowersCount;
        this.minMediaCount = minMediaCount;
    }

    public void setChannelValues(Integer accFollowersCount, Integer accMediaCount) {
        this.accFollowersCount = accFollowersCount;
        this.accMediaCount = accMediaCount;
    }
}
