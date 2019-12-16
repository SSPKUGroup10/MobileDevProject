package com.example.mobiledevproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private int userId;

    private String userName;

    private String password;

    private List<GroupCreate> joinedCircles;
    private List<GroupCreate> otherCircles;

    public List<GroupCreate> getJoinedCircles() {
        return joinedCircles;
    }

    public void setJoinedCircles(List<GroupCreate> joinedCircles) {
        this.joinedCircles = joinedCircles;
    }

    public List<GroupCreate> getOtherCircles() {
        return otherCircles;
    }

    public void setOtherCircles(List<GroupCreate> otherCircles) {
        this.otherCircles = otherCircles;
    }

    public User(){

    }

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;

    }

    public User(UserCreate userCreate){
        this.userName = userCreate.getUserName();
        this.password = userCreate.getPassword();
        this.userId = userCreate.getUserId();
        joinedCircles = new ArrayList<>();
        otherCircles = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", joinedCircles=" + joinedCircles +
                ", otherCircles=" + otherCircles +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
