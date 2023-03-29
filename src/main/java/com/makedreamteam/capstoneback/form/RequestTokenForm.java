package com.makedreamteam.capstoneback.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestTokenForm {
    private UUID userId;
    private String refreshToken;
}
