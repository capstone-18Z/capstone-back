package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

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
    private String title;
    @Column
    private Long userId; // 외래키 설정

    @Column
    private String createDate;

    @Column
    private String updateDate;

    @Column
    private String detail;

    @Column
    private int period;

    @Column
    private Long teamLeader;




    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamid) {
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
