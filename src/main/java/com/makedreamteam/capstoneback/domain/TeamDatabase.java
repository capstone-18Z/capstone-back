package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDatabase {
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "team")
    @MapsId
    private Team team;

    @ColumnDefault("0")
    private int mysqlL;

    @ColumnDefault("0")
    private int mariadbL;

    @ColumnDefault("0")
    private int mongodbL;

    @ColumnDefault("0")
    private int schemaL;
}
