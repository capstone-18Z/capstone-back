package com.makedreamteam.capstoneback.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberData {
    private Object allMemberList;
    private Object recommendList;
    private Object Member;
    private Object PostMember;
    private Object Token;
}
