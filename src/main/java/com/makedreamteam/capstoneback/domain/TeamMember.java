package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Builder
@Getter
@Setter
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



}


