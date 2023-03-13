package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.Builder;


@Entity
@Builder
public class TeamMember {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long teamMemberId;

    @Column
    private Long teamLeader;

    @Column
    private Long teamId;

    @Column
    private Long userId;

    public TeamMember(Long teamMemberId, Long teamLeader, Long teamId, Long userId) {
        this.teamMemberId = teamMemberId;
        this.teamLeader = teamLeader;
        this.teamId = teamId;
        this.userId = userId;
    }

    public TeamMember() {

    }


    public Long getTeamId() {
        return teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Long getTeamMemberId() {
        return teamMemberId;
    }

    public Long getTeamLeader() {
        return teamLeader;
    }

    public void setTeamMemberId(Long teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public void setTeamLeader(Long teamLeader) {
        this.teamLeader = teamLeader;
    }
}


