package com.makedreamteam.capstoneback.controller;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    private String writer;

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

}
