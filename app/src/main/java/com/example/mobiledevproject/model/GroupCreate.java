package com.example.mobiledevproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GroupCreate implements Serializable {

    @Expose(serialize = false)
    private int groupId;


    @Expose
    @SerializedName("name")
    private String groupName;
    @Expose
    private String type;
    @Expose
    private String startAt;
    @Expose
    private String endAt;

    @Expose
    @SerializedName("desc")
    private String description;
    @Expose
    private String checkRule;

    @Expose
    @SerializedName("circleMasterId")
    private int masterId = -1;

    @Override
    public String toString() {
        return "GroupCreate{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", type='" + type + '\'' +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", description='" + description + '\'' +
                ", checkRule='" + checkRule + '\'' +
                ", masterId=" + masterId +
                '}';
    }

    public GroupCreate(){

    }

    public GroupCreate(String groupName, String description){
        this.groupName = groupName;
        this.description = description;
    }

    public boolean isCompleted(){
        if("".equals(groupName) || "".equals(description)
        || "".equals(checkRule) || "".equals(type)
        || "".equals(startAt) || "".equals(endAt)){
            return false;
        } return true;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheckRule() {
        return checkRule;
    }

    public void setCheckRule(String checkRule) {
        this.checkRule = checkRule;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




}
