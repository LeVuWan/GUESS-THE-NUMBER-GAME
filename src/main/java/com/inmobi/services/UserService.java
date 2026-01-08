package com.inmobi.services;

import com.inmobi.dtos.res.TurnResponse;

public interface UserService {
    TurnResponse userTurn(Long userId, int userGuess);
}
