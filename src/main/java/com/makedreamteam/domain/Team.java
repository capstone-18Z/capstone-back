package com.makedreamteam.domain;

import jakarta.persistence.*;

@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamid;

    @Column
    private int current_fm;

    @Column
    private int current_bm;

    @Column
    private int wanted_fm;

    @Column
    private int wanted_bm;

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

    public int getCurrent_bm() {
        return current_bm;
    }

    public void setCurrent_bm(int current_dm) {
        this.current_bm = current_dm;
    }

    public int getWanted_fm() {
        return wanted_fm;
    }

    public void setWanted_fm(int wanted_fm) {
        this.wanted_fm = wanted_fm;
    }

    public int getWanted_bm() {
        return wanted_bm;
    }

    public void setWanted_bm(int wanted_dm) {
        this.wanted_bm = wanted_dm;
    }
}
