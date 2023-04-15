package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class MemberLang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

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
}