package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Team{
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID teamId;

    @Column
    @ColumnDefault("0")
    private int currentFrontMember;

    @Column
    @ColumnDefault("0")
    private int postNumber;

    @Column
    @ColumnDefault("0")
    private int currentBackMember;

    @Column
    @ColumnDefault("0")
    private int wantedFrontMember;

    @Column
    @ColumnDefault("0")
    private int wantedBackEndMember;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String title;
    @Column
    private UUID userId; // 외래키 설정

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String createDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String updateDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String detail;

    @Column
    private int period;

    @Column
    private UUID teamLeader;
}
