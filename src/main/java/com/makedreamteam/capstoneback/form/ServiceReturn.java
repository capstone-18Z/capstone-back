package com.makedreamteam.capstoneback.form;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceReturn {
   private Object data;
   private String newToken;
}
