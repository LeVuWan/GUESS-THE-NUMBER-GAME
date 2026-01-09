package com.inmobi.services;

import java.util.List;

import com.inmobi.dtos.res.GetTopUsersByScore;
import com.inmobi.dtos.res.TurnResponse;
import com.inmobi.dtos.res.UserDetailRes;

public interface UserService {
    TurnResponse userTurn(Long userId, int userGuess);

    List<GetTopUsersByScore> getTop10UsersByScore();

    UserDetailRes getUserDetail(Long id);
}
