package com.inmobi.dtos.res;

public class UserDetailRes {
    private Long userId;
    private String username;
    private Integer turn;
    private Integer score;

    public UserDetailRes(Long userId, String username, Integer turn, Integer score) {
        this.userId = userId;
        this.username = username;
        this.turn = turn;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public UserDetailRes() {
    }

}
