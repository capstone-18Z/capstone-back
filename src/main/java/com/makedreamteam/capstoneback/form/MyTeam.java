package com.makedreamteam.capstoneback.form;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyTeam {
    private UUID teamId;
    private String title;
}
