package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDatabase {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name="team")
    private Team team;

    @ColumnDefault("0")
    private int msq;

    @ColumnDefault("0")
    private int mariadb;

    @ColumnDefault("0")
    private int mongodb;

    @ColumnDefault("0")
    private int d_design;
}
