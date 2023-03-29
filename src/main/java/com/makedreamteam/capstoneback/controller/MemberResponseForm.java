package com.makedreamteam.capstoneback.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberResponseForm {
    private int state;
    private String message;
    private MemberData data;
}
