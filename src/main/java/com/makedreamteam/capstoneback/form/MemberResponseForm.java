package com.makedreamteam.capstoneback.form;

import com.makedreamteam.capstoneback.controller.MemberData;
import com.makedreamteam.capstoneback.domain.SolvedAcUser;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberResponseForm {
    private int state;
    private String message;
    private MemberData data;
    private SolvedAcUser solvedAcUser;
}
