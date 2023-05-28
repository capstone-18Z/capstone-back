package com.makedreamteam.capstoneback.form;

import com.makedreamteam.capstoneback.domain.Team;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceReturn {
   private Team data;
   private String newToken;
}
