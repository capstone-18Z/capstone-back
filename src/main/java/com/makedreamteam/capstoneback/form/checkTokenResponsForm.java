package com.makedreamteam.capstoneback.form;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class checkTokenResponsForm {
    private UUID userId;
    private String newToken;
}
