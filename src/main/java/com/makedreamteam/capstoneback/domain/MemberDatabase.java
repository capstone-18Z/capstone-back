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
public class MemberDatabase {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ColumnDefault("0")
    private int msq;

    @ColumnDefault("0")
    private int mariadb;

    @ColumnDefault("0")
    private int mongodb;

    @ColumnDefault("0")
    private int d_design;

    /*public MemberDatabase(Member member) {
        this.member = member;
    }*/
}
