package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TeamData {
    private Object dataWithoutLogin;
    private Object dataWithLogin;
}
