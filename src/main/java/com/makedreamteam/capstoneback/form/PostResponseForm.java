package com.makedreamteam.capstoneback.form;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Data
@Getter
@Setter
public class PostResponseForm {
    private int state;
    private String message;
    private Object data;
    private Long pid;
    private List<String> filenames;
    private boolean updatable;
}

