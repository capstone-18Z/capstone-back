package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "teamId"})})
public class WaitingListOfMatching {
    @Id
    private UUID waitingId;

    @Column
    private UUID userId;

    @Column
    private UUID teamId;

}
