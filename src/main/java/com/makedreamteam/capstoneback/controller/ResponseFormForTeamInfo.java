package com.makedreamteam.capstoneback.controller;

import lombok.Builder;

@Builder
public class ResponseFormForTeamInfo {
    private int state;
    private String message;
    private Object teamInfo;


    @Override
    public String toString() {
        return "ResponseFormForTeamInfo{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", teamInfo=" + teamInfo +
                '}';
    }
}
