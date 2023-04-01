package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "teamId"})})
public class WaitingListOfMatching {
    @Id
    private UUID waitingId;

    @Column
    private UUID userId;

    @Column
    private UUID teamId;

    @Column
    private Long postId;

}
