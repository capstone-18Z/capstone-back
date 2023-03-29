package com.makedreamteam.capstoneback.form;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewToken {
    private int state;
    private String message;
    private String loginToken;
    private String refreshToken;
}
