package com.makedreamteam.domain;

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
    private int sql_lang;


    public Long getTeamid() {
        return teamid;
    }

    public int getPython() {
        return python;
    }

    public int getC() {
        return c;
    }

    public int getJava() {
        return java;
    }

    public int getCpp() {
        return cpp;
    }

    public int getCs() {
        return cs;
    }

    public int getVb() {
        return vb;
    }

    public int getJavascript() {
        return javascript;
    }

    public int getAssembly() {
        return assembly;
    }

    public int getPhp() {
        return php;
    }


    public void setTeamid(Long teamid) {
        this.teamid = teamid;
    }

    public void setPython(int python) {
        this.python = python;
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

    public void setAssembly(int assembly) {
        this.assembly = assembly;
    }

    public void setPhp(int php) {
        this.php = php;
    }


    public int getSql_lang() {
        return sql_lang;
    }

    public void setSql_lang(int sql_lang) {
        this.sql_lang = sql_lang;
    }
}
