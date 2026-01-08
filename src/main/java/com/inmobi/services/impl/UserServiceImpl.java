package com.inmobi.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.Exception.TurnOverException;
import com.inmobi.configs.LeaderboardCache;
import com.inmobi.dtos.res.GetTopUsersByScore;
import com.inmobi.dtos.res.TurnResponse;
import com.inmobi.dtos.res.UserDetailRes;
import com.inmobi.models.User;
import com.inmobi.models.cache.TopUser;
import com.inmobi.repositories.UserRepository;
import com.inmobi.services.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Random random = new Random();
    private final LeaderboardCache leaderboardCache;

    public UserServiceImpl(UserRepository userRepository, LeaderboardCache leaderboardCache) {
        this.userRepository = userRepository;
        this.leaderboardCache = leaderboardCache;
    }

    @Override
    public TurnResponse userTurn(Long userId, int userGuess) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getTurn() <= 0) {
            throw new TurnOverException("You've run out of turns. Buy more!");
        }

        user.setTurn(user.getTurn() - 1);

        int winChance = random.nextInt(100);
        int serverNumber;
        boolean isWin = false;

        if (winChance < 5) {
            serverNumber = userGuess;
            isWin = true;
            user.setScore(user.getScore() + 1);
        } else {
            serverNumber = generateWrongNumber(userGuess);
        }

        if (isWin) {
            userRepository.save(user);

            leaderboardCache.checkAndUpdate(new TopUser(
                    user.getId(),
                    user.getUsername(),
                    user.getScore()));
        }

        return new TurnResponse(userGuess,
                serverNumber,
                isWin,
                user.getTurn(),
                user.getScore());
    }

    private int generateWrongNumber(int userGuess) {
        int result;
        do {
            result = random.nextInt(5) + 1;
        } while (result == userGuess);
        return result;
    }

    @Override
    @Transactional
    public void create100Users() {
        long startIndex = userRepository
                .findTopByOrderByIdDesc()
                .map(User::getId)
                .orElse(0L);

        List<User> users = new ArrayList<>(100);

        for (int i = 1; i <= 10000; i++) {
            User user = new User();

            user.setUsername("user_" + (startIndex + i));
            user.setScore(ThreadLocalRandom.current().nextInt(20, 31));
            user.setTurn(0);

            users.add(user);
        }

        userRepository.saveAll(users);
    }

    @Override
    public List<GetTopUsersByScore> getTop10UsersByScore() {
        Pageable topTenByScore = PageRequest.of(0, 10, Sort.by("score").descending());

        return userRepository.findAllTopUsers(topTenByScore).getContent();
    }

    public UserDetailRes getUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserDetailRes(user.getId(), user.getUsername(), user.getTurn(), user.getScore());
    };

}
