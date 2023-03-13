package com.makedreamteam.capstoneback.controller;

import jakarta.persistence.Column;

public class UserForm {
    private Long userid;

    private String password;

    private String name;

    private String detail;

    private int python;
    private int c;
    private int java;
    private int cpp;
    private int cs;
    private int vb;
    private int javascript;
    private int assembly;
    private int php;
    private int sqlLang;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public int getSqlLang() {
        return sqlLang;
    }

    public void setSqlLang(int sqlLang) {
        this.sqlLang = sqlLang;
    }
}
