package com.inmobi.dtos.res;

public class TurnResponse {
    private int userGuess;
    private int serverNumber;
    private boolean isWin;
    private int remainingTurns;
    private int currentScore;

    public TurnResponse() {
    }

    public TurnResponse(int userGuess, int serverNumber, boolean isWin, int remainingTurns, int currentScore) {
        this.userGuess = userGuess;
        this.serverNumber = serverNumber;
        this.isWin = isWin;
        this.remainingTurns = remainingTurns;
        this.currentScore = currentScore;
    }

    public int getUserGuess() {
        return userGuess;
    }

    public void setUserGuess(int userGuess) {
        this.userGuess = userGuess;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean isWin) {
        this.isWin = isWin;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

}
