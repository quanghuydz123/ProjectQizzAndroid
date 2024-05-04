package com.example.projectqizz.Model;

public class TestModel {
    private String testID;
    private int topScore;
    private int time;
    private String name;

    public TestModel(String testID, int topScore, int time,String name) {
        this.testID = testID;
        this.topScore = topScore;
        this.time = time;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public int getTopScore() {
        return topScore;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
