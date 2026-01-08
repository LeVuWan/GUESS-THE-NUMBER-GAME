package com.inmobi.services.impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.Exception.TurnOverException;
import com.inmobi.dtos.res.TurnResponse;
import com.inmobi.models.User;
import com.inmobi.repositories.UserRepository;
import com.inmobi.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Random random = new Random();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.save(user);

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

}
