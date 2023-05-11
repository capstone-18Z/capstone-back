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
public class TeamFramework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ColumnDefault("0")
    private int react;

    @ColumnDefault("0")
    private int android;

    @ColumnDefault("0")
    private int node;

    @ColumnDefault("0")
    private int xcode;

    @ColumnDefault("0")
    private int spring;

    @ColumnDefault("0")
    private int unity;

    @ColumnDefault("0")
    private int unreal;

    @ColumnDefault("0")
    private int tdmax;

    @OneToOne
    @JoinColumn(name="team_id")
    private Team team;
}
