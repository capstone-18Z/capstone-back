package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TeamLang {
    @Id
    private UUID teamId;

    @Column
    @ColumnDefault("0")
    private int python;

    @Column
    @ColumnDefault("0")
    private int c;

    @Column
    @ColumnDefault("0")
    private int java;

    @Column
    @ColumnDefault("0")
    private int cpp;

    @Column
    @ColumnDefault("0")
    private int cs;

    @Column
    @ColumnDefault("0")
    private int vb;

    @Column
    @ColumnDefault("0")
    private int javascript;

    @Column
    @ColumnDefault("0")
    private int assembly;

    @Column
    @ColumnDefault("0")
    private int php;

    @Column
    @ColumnDefault("0")
    private int sqllang;


}
