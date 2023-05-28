package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class RefreshToken {

    @Id
    private UUID userId;

    @Column
    private String refreshToken;



}
