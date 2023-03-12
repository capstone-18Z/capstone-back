package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@IdClass(TeamMember.TeamMemberId.class)
public class TeamMember {
    @Id

    private Long teamId;

    @Id

    private Long userId;

    public TeamMember(Long teamId, Long userId) {
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


        public static class  TeamMemberId implements Serializable {

        private Long teamId;

        private Long userId;

        public TeamMemberId(Long teamId, Long userId) {
            this.teamId = teamId;
            this.userId = userId;
        }
        public TeamMemberId(){}

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
    }
}


