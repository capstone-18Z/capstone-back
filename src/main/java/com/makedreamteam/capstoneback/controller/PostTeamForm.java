package com.makedreamteam.capstoneback.controller;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PostTeamForm {
    //현재 모집된 멤버 수
    private int currentFrontMember;
    private int currentBackMember;

    //모집 할 멤버  수
    private int wantedFrontMember;
    private int wantedBackEndMember;

    private UUID teamLeader;


    private String title;
    private String createDate;
    private String updateDate;
    private String detail;
    private int period;

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




    public int getWantedFrontMember() {
        return wantedFrontMember;
    }

    public int getWantedBackEndMember() {
        return wantedBackEndMember;
    }

    public String getTitle() {
        return title;
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

    public int getSqlLang() {
        return sqlLang;
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

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPeriod(int period) {
        this.period = period;
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

    public void setSqlLang(int sqlLang) {
        this.sqlLang = sqlLang;
    }

    @Override
    public String toString() {
        return "PostTeamForm{" +
                "currentFrontMember=" + currentFrontMember +
                ", currentBackEndMember=" + currentBackMember +
                ", wantedFrontMember=" + wantedFrontMember +
                ", wantedBackEndMember=" + wantedBackEndMember +
                ", title='" + title + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", detail='" + detail + '\'' +
                ", period=" + period +
                ", python=" + python +
                ", c=" + c +
                ", java=" + java +
                ", cpp=" + cpp +
                ", cs=" + cs +
                ", vb=" + vb +
                ", javascript=" + javascript +
                ", assembly=" + assembly +
                ", php=" + php +
                ", sqlLang=" + sqlLang +
                '}';
    }

    public int getCurrentFrontMember() {
        return currentFrontMember;
    }

    public int getCurrentBackMember() {
        return currentBackMember;
    }

    public void setCurrentFrontMember(int currentFrontMember) {
        this.currentFrontMember = currentFrontMember;
    }

    public void setCurrentBackMember(int currentBackMember) {
        this.currentBackMember = currentBackMember;
    }

    public UUID getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(UUID teamLeader) {
        this.teamLeader = teamLeader;
    }
}
