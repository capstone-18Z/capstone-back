package com.makedreamteam.capstoneback.form;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class RequestData {
    private Object data;
    private long matchId;
}
