package com.thegymgoers_java.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("gymgroups")
public class GymGroup {

    @Id
    @JsonProperty("_id")
    private String _id;


    @JsonProperty("groupName")
    @NotEmpty(message = "GymGroup must have a name")
    private String groupName;

    @NotEmpty
    @JsonProperty("admins")
    private List<String> admins = new ArrayList<>();

    @JsonProperty("members")
    private List<String> members = new ArrayList<>();


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public @NotEmpty String getGroupName() {
        return groupName;
    }

    public void setGroupName(@NotEmpty String groupName) {
        this.groupName = groupName;
    }

    public @NotEmpty List<String> getAdmins() {
        return admins;
    }

    public void addAdmins(@NotEmpty String adminsId) {
        this.admins.add(adminsId);
    }

    public List<String> getMembers() {
        return members;
    }

    public void addMember(@NotEmpty String membersId) {
        this.members.add(membersId);
    }

    @Override
    public String toString() {
        return String.format("GymGroup{_id='%s', groupName='%s', admins=%s, members=%s}",
                _id, groupName, admins, members);
    }
}
