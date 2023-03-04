package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TeamLang {
    @Id
    private Long teamid;

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





    public int getPython() {
        return python;
    }

    public void setPython(int python) {
        this.python = python;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getJava() {
        return java;
    }

    public void setJava(int java) {
        this.java = java;
    }

    public int getCpp() {
        return cpp;
    }

    public void setCpp(int cpp) {
        this.cpp = cpp;
    }

    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public int getVb() {
        return vb;
    }

    public void setVb(int vb) {
        this.vb = vb;
    }

    public int getJavascript() {
        return javascript;
    }

    public void setJavascript(int javascript) {
        this.javascript = javascript;
    }

    public int getAssembly() {
        return assembly;
    }

    public void setAssembly(int assembly) {
        this.assembly = assembly;
    }

    public int getPhp() {
        return php;
    }

    public void setPhp(int php) {
        this.php = php;
    }

    public int getSqllang() {
        return sqllang;
    }

    public void setSqllang(int sqllang) {
        this.sqllang = sqllang;
    }

    public void setTeamid(Long teamid) {
        this.teamid = teamid;
    }

    @Override
    public String toString() {
        return "TeamLang{" +
                "teamid=" + teamid +
                ", python=" + python +
                ", c=" + c +
                ", java=" + java +
                ", cpp=" + cpp +
                ", cs=" + cs +
                ", vb=" + vb +
                ", javascript=" + javascript +
                ", assembly=" + assembly +
                ", php=" + php +
                ", sqllang=" + sqllang +
                '}';
    }

    public Long getTeamid() {
        return teamid;
    }
}
