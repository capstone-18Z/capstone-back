package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.reflection.qual.UnknownClass;
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
    private UUID teamLeader;

    @Column
    @ColumnDefault("0")
    private int wantedBasicMember;

    @Column
    @ColumnDefault("0")
    private int currentBasicMember;

    @Column
    @ColumnDefault("0")
    private int currentFrontMember;

    @Column
    @ColumnDefault("0")
    private int currentBackMember;

    @Column
    @ColumnDefault("0")
    private int wantedFrontMember;

    @Column
    @ColumnDefault("0")
    private int wantedBackEndMember;

    @Column
    private int field;// 1: 캡스톤 2: 일반 교과목

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String title;
    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String writer;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String createDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String updateDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String detail;

    @Column
    private int period;

    @Column
    @ColumnDefault("0")
    private int python;

    @Column
    @ColumnDefault("0")
    private int c;

    @Column
    @ColumnDefault("0")
    private int java;

    @Column
    @ColumnDefault("0")
    private int cpp;

    @Column
    @ColumnDefault("0")
    private int cs;

    @Column
    @ColumnDefault("0")
    private int vb;

    @Column
    @ColumnDefault("0")
    private int javascript;

    @Column
    @ColumnDefault("0")
    private int assembly;

    @Column
    @ColumnDefault("0")
    private int php;

    @Column
    @ColumnDefault("0")
    private int sqllang;
}
