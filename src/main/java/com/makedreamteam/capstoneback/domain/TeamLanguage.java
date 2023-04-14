package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class TeamLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ColumnDefault("0")
    private int c;

    @ColumnDefault("0")
    private int cpp;

    @ColumnDefault("0")
    private int cs;

    @ColumnDefault("0")
    private int java;

    @ColumnDefault("0")
    private int javascript;

    @ColumnDefault("0")
    private int sql_Lang;

    @ColumnDefault("0")
    private int swift;

    @ColumnDefault("0")
    private int kotlin;

    @ColumnDefault("0")
    private int typescript;

    @ColumnDefault("0")
    private int python;

    @ColumnDefault("0")
    private int html;

    @ColumnDefault("0")
    private int R;

    @OneToOne
    @MapsId
    @JoinColumn(name = "teamId")
    private Team team;
}
