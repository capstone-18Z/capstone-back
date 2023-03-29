package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Team{
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)" ,name="team_id")
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


    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<TeamKeyword> teamKeywords = new ArrayList<>();

    @JsonProperty("teamKeywords")
    public List<String> getKeywordValues() {
        return teamKeywords.stream().map(TeamKeyword::getValue).collect(Collectors.toList());
    }

    public void addKeyword(TeamKeyword teamKeyword) {
        this.teamKeywords.add(teamKeyword);
        teamKeyword.setTeam(this);
    }

    public void removeKeyword(TeamKeyword teamKeyword) {
        this.teamKeywords.remove(teamKeyword);
        teamKeyword.setTeam(null);
    }

    public void setTeam(TeamKeyword teamKeyword){
        teamKeyword.setTeam(this);
    }
}
