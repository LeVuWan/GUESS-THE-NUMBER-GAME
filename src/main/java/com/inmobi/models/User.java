package com.inmobi.models;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "tbl_users")
public class User extends AbstractEntity implements Serializable {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    private Integer score;

    private Integer turn;

    private String refreshToken;

    public User() {
    }

    public User(String username, String password, Integer score, Integer turn, String refreshToken) {
        this.username = username;
        this.password = password;
        this.score = score;
        this.turn = turn;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

}
