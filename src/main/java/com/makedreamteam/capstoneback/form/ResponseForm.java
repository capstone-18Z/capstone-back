package com.makedreamteam.capstoneback.form;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class ResponseForm {
    private int state;
    private String message;
    private Object data;
    private boolean updatable;
}
