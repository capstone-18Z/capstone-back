package com.makedreamteam.capstoneback.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseFormForTeamInfo {
    private int state;
    private String message;
    private TeamData data;
}
