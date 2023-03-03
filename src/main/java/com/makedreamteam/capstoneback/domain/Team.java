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
    private Long teamid;

    @Column
    @ColumnDefault("0")
    private int current_fm;

    @Column
    @ColumnDefault("0")
    private int current_bm;

    @Column
    @ColumnDefault("0")
    private int wanted_fm;

    @Column
    @ColumnDefault("0")
    private int wanted_bm;

    @Column
    private String title;
    @Column
    private Long userid; // 외래키 설정

    @Column
    private String createdate;

    @Column
    private String updatedate;

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

    public String getTitle() {
        return title;
    }

    public Long getUserid() {
        return userid;
    }

    public String getCreatedate() {
        return createdate;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public String getDetail() {
        return detail;
    }

    public int getPeriod() {
        return period;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
