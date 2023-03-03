package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ResponseFormForTeamInfo {
    private int state;
    private String message;
    private Object dataList;



}
