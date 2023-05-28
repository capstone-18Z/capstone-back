package com.makedreamteam.capstoneback.controller;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Data
public class MemberData {
    private Object allMemberList;
    private Object recommendList;
    private Object Member;
    private Object PostMember;
    private Object Token;
}
