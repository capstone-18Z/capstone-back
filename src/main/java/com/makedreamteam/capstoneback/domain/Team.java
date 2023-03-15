package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private Long userId; // 외래키 설정

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String createDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String updateDate;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String detail;

    @Column
    private int period;

    @Column
    private Long teamLeader;




    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamid) {
        this.teamId = teamid;
    }



    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getCurrentFrontMember() {
        return currentFrontMember;
    }

    public int getCurrentBackMember() {
        return currentBackMember;
    }

    public int getWantedFrontMember() {
        return wantedFrontMember;
    }

    public int getWantedBackEndMember() {
        return wantedBackEndMember;
    }

    public String getTitle() {
        return title;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getDetail() {
        return detail;
    }

    public int getPeriod() {
        return period;
    }

    public Long getTeamLeader() {
        return teamLeader;
    }

    public void setCurrentFrontMember(int currentFrontMember) {
        this.currentFrontMember = currentFrontMember;
    }

    public void setCurrentBackMember(int currentBackMember) {
        this.currentBackMember = currentBackMember;
    }

    public void setWantedFrontMember(int wantedFrontMember) {
        this.wantedFrontMember = wantedFrontMember;
    }

    public void setWantedBackEndMember(int wantedBackEndMember) {
        this.wantedBackEndMember = wantedBackEndMember;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public void setTeamLeader(Long teamLeader) {
        this.teamLeader = teamLeader;
    }
}
