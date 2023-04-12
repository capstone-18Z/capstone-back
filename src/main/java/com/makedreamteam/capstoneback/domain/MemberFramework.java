package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class MemberFramework {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @MapsId // Member 엔티티의 id 필드와 매핑
    private Member member;

    @ColumnDefault("0")
    private int react;

    @ColumnDefault("0")
    private int node;

    @ColumnDefault("0")
    private int xcode;

    @ColumnDefault("0")
    private int android;

    @ColumnDefault("0")
    private int spring;

    @ColumnDefault("0")
    private int unity;

    @ColumnDefault("0")
    private int unreal;

    @ColumnDefault("0")
    private int tdmax;
}
