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
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @MapsId // Member 엔티티의 id 필드와 매핑
    private Member member;

    @ColumnDefault("0")
    private int python;

    @ColumnDefault("0")
    private int c;

    @ColumnDefault("0")
    private int java;

    @ColumnDefault("0")
    private int cpp;

    @ColumnDefault("0")
    private int cs;

    @ColumnDefault("0")
    private int javascript;

    @ColumnDefault("0")
    private int typescript;

    @ColumnDefault("0")
    private int html;

    @ColumnDefault("0")
    private int swift;
}