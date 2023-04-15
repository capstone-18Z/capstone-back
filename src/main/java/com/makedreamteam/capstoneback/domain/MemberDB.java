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
    @MapsId
    private Member member;

    @ColumnDefault("0")
    private int mysqlL;

    @ColumnDefault("0")
    private int mariadbL;

    @ColumnDefault("0")
    private int mongodbL;

    @ColumnDefault("0")
    private int schemaL;
}
