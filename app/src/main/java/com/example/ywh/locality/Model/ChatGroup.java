package com.example.ywh.locality.Model;

import java.util.List;

public class ChatGroup {
    private String GroupID;
    private String GroupName;
    private String Owner;
    private List<String> JoinedMemberList;
    private String Search;
    private String GroupIcon;

    public ChatGroup() {
    }

    public ChatGroup(String groupName, String owner) {
        GroupName = groupName;
        Owner = owner;
        Search = groupName.toLowerCase();
        GroupIcon = "default";
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public List<String> getJoinedMemberList() {
        return JoinedMemberList;
    }

    public void setJoinedMemberList(List<String> joinedMemberList) {
        JoinedMemberList = joinedMemberList;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(String search) {
        Search = search;
    }

    public String getGroupIcon() {
        return GroupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        GroupIcon = groupIcon;
    }

}
