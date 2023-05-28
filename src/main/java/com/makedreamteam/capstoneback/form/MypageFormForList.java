package com.makedreamteam.capstoneback.form;

import lombok.*;

import java.util.UUID;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MypageFormForList {
    private String message;
    private UUID id;
    private Object info;
    private String field;
}
