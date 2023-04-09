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
@Table(indexes = {@Index(name = "idx_title", columnList = "title")})//게시물 검색 시 제목을 주로 검색을 하므로 title을 인덱싱한다
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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<TeamKeyword> teamKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<WaitingListOfMatchingUserToTeam> requestList=new ArrayList<>();

    @JsonProperty("teamKeywords")
    public List<String> getKeywordValues() {
        return teamKeywords.stream().map(TeamKeyword::getValue).collect(Collectors.toList());
    }
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imagePaths;

    public void addKeyword(TeamKeyword teamKeyword) {
        this.teamKeywords.add(teamKeyword);
        teamKeyword.setTeam(this);
    }

    public void removeKeyword(TeamKeyword teamKeyword) {
        this.teamKeywords.remove(teamKeyword);
        teamKeyword.setTeam(null);
    }

    public void setKeywordTeam(TeamKeyword teamKeyword){
        teamKeyword.setTeam(this);
    }
    public void setWaitingListTeam(WaitingListOfMatchingUserToTeam waitingListOfMatchingUserToTeam){waitingListOfMatchingUserToTeam.setTeam(this);}

}
