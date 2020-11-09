package com.alexcorp.bloggers.repository;

import com.alexcorp.bloggers.domain.YouTubeChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeChannelRepository extends JpaRepository<YouTubeChannel, String> {

}
