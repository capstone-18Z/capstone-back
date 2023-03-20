package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.Builder;

import java.util.UUID;


@Entity
@Builder
public class TeamMember {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID teamMemberId;

    @Column
    private UUID teamLeader;

    @Column
    private UUID teamId;

    @Column
    private UUID userId;

    public TeamMember(UUID teamMemberId, UUID teamLeader, UUID teamId, UUID userId) {
        this.teamMemberId = teamMemberId;
        this.teamLeader = teamLeader;
        this.teamId = teamId;
        this.userId = userId;
    }

    public TeamMember() {

    }


    public UUID getTeamId() {
        return teamId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }


    public UUID getTeamMemberId() {
        return teamMemberId;
    }

    public UUID getTeamLeader() {
        return teamLeader;
    }

    public void setTeamMemberId(UUID teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public void setTeamLeader(UUID teamLeader) {
        this.teamLeader = teamLeader;
    }
}


