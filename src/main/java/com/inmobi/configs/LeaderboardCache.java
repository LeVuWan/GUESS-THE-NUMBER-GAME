package com.inmobi.configs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Component;

import com.inmobi.models.cache.TopUser;

@Component
public class LeaderboardCache {
    private final Set<TopUser> topUsers = new ConcurrentSkipListSet<>(
            Comparator.comparing(TopUser::getScore).reversed()
                    .thenComparing(TopUser::getUsername));

    private static final int TOP_LIMIT = 10;

    public synchronized void updateCache(List<TopUser> usersFromDb) {
        topUsers.clear();
        topUsers.addAll(usersFromDb);
    }

    public List<TopUser> getLeaderboard() {
        return new ArrayList<>(topUsers);
    }

    public synchronized void checkAndUpdate(TopUser currentUser) {
        if (topUsers.size() < TOP_LIMIT || currentUser.getScore() > getLowScore()) {

            topUsers.removeIf(u -> u.getId().equals(currentUser.getId()));

            topUsers.add(currentUser);

            if (topUsers.size() > TOP_LIMIT) {
                topUsers.remove(topUsers.stream().reduce((first, second) -> second).orElse(null));
            }
        }
    }

    private Integer getLowScore() {
        if (topUsers.isEmpty())
            return 0;
        return topUsers.stream().reduce((first, second) -> second).get().getScore();
    }
}
