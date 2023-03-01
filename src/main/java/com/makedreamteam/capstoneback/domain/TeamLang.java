package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class TeamLang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Long getUserid() {
        return teamid;
    }

    public void setUserid(Long userid) {
        this.teamid = userid;
    }

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

    public void setJava(int java) {
        this.java = java;
    }

    public void setCpp(int cpp) {
        this.cpp = cpp;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public void setVb(int vb) {
        this.vb = vb;
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
}
