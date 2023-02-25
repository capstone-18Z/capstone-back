package com.makedreamteam.domain;

import jakarta.persistence.*;

@Entity
public class PostTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamid; // 외래키 설정

    @Column
    private Long userid; // 외래키 설정

    @Column
    private String createdat;

    @Column
    private String updatedat;

    @Column
    private String detail;

    @Column
    private int period;

    public Long getTeamid() {
        return teamid;
    }

    public void setTeamid(Long teamid) {
        this.teamid = teamid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(String updatedat) {
        this.updatedat = updatedat;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
