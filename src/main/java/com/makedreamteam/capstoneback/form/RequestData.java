package com.makedreamteam.capstoneback.form;

import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class RequestData {
    private Object data;
    private UUID matchId;
    private UUID teamLeader;
}
