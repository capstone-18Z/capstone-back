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
public class MemberDB {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @MapsId // Member 엔티티의 id 필드와 매핑
    private Member member;

    @ColumnDefault("0")
    private int mysql;

    @ColumnDefault("0")
    private int mariadb;

    @ColumnDefault("0")
    private int mongodb;

    @ColumnDefault("0")
    private int schema;
}
