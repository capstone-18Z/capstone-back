package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberKeyword {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JsonIgnore
    private UUID id;

    @Column(columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String category;

    @Column(columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String field;

    @Column(columnDefinition = "VARCHAR(4) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String sub="none";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

}
