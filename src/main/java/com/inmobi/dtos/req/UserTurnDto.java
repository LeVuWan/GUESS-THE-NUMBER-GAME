package com.inmobi.dtos.req;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UserTurnDto implements Serializable {
    @Min(value = 1, message = "User guess must be at least 1")
    @Max(value = 5, message = "User guess must be at most 5")
    private int userGuess;

    public UserTurnDto(int userGuess) {
        this.userGuess = userGuess;
    }

    public int getUserGuess() {
        return userGuess;
    }

    public void setUserGuess(int userGuess) {
        this.userGuess = userGuess;
    }

}
