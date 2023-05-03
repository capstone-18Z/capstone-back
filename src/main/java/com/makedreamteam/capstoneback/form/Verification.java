package com.makedreamteam.capstoneback.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Verification {
    private String code;
    private boolean isVerified;
    public Verification(){
        isVerified=false;
    }
}
