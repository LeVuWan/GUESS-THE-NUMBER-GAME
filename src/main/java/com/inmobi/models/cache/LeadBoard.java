package com.inmobi.models.cache;

import java.util.Map;

public class LeadBoard {
    private Integer hightScore;
    private Integer lowScore;
    private Map<String, TopUser> users;

    public LeadBoard() {
    }

    public LeadBoard(Integer hightScore, Map<String, TopUser> users, Integer lowScore) {
        this.hightScore = hightScore;
        this.users = users;
        this.lowScore = lowScore;
    }

    public Integer getHightScore() {
        return hightScore;
    }

    public void setHightScore(Integer hightScore) {
        this.hightScore = hightScore;
    }

    public Map<String, TopUser> getUsers() {
        return users;
    }

    public void setUsers(Map<String, TopUser> users) {
        this.users = users;
    }

    public Integer getLowScore() {
        return lowScore;
    }

    public void setLowScore(Integer lowScore) {
        this.lowScore = lowScore;
    }

}
