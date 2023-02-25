package com.makedreamteam.domain;

import jakarta.persistence.*;

@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamid;

    @Column
    private int current_fm;

    @Column
    private int current_dm;

    @Column
    private String detail;

    @Column
    private int period;

    @Column
    private int wanted_fm;

    @Column
    private int wanted_dm;

    public Long getTeamid() {
        return teamid;
    }

    public void setTeamid(Long teamid) {
        this.teamid = teamid;
    }

    public int getCurrent_fm() {
        return current_fm;
    }

    public void setCurrent_fm(int current_fm) {
        this.current_fm = current_fm;
    }

    public int getCurrent_dm() {
        return current_dm;
    }

    public void setCurrent_dm(int current_dm) {
        this.current_dm = current_dm;
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

    public int getWanted_fm() {
        return wanted_fm;
    }

    public void setWanted_fm(int wanted_fm) {
        this.wanted_fm = wanted_fm;
    }

    public int getWanted_dm() {
        return wanted_dm;
    }

    public void setWanted_dm(int wanted_dm) {
        this.wanted_dm = wanted_dm;
    }
}
