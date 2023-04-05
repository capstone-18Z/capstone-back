package com.makedreamteam.capstoneback.domain;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Token {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private Date exp;
    private UUID key;
}