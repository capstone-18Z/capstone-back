package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @Column
    private UUID userId;
    @Column
    private String nickname;
    @Column
    private String title;
    @Column
    private String detail;
    @Column
    private int field;//1 : 프론트 2 : 백 , 3 : 구분없음
}
