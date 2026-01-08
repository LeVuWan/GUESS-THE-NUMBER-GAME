package com.inmobi.models.cache;

public class TopUser {
    private Long id;
    private String username;
    private Integer score;

    public TopUser() {
    }

    public TopUser(Long id, String username, Integer score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}
