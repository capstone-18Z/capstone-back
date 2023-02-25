package com.makedreamteam.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class UserLang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

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
    private int sql;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
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

    public int getSql() {
        return sql;
    }

    public void setSql(int sql) {
        this.sql = sql;
    }
}
