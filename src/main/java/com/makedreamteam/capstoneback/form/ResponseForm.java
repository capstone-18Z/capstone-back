package com.makedreamteam.capstoneback.form;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseForm {
    private int state;
    private String message;
    private TeamData data;
    private boolean updatable;
    private String newToken;
}
