package com.inmobi.configs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.inmobi.dtos.res.GetTopUsersByScore;
import com.inmobi.models.cache.TopUser;
import com.inmobi.services.UserService;

@Component
public class DataInitializer {
    private final UserService userService;
    private final LeaderboardCache leaderboardCache;

    public DataInitializer(UserService userService, LeaderboardCache leaderboardCache) {
        this.userService = userService;
        this.leaderboardCache = leaderboardCache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initLeaderboard() {
        List<GetTopUsersByScore> topUsers = userService.getTop10UsersByScore();

        List<TopUser> dtos = topUsers.stream()
                .map(u -> new TopUser(u.getId(), u.getUsername(), u.getScore()))
                .collect(Collectors.toList());

        leaderboardCache.updateCache(dtos);
    }
}
