package com.makedreamteam.capstoneback.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;



@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {@Index(name = "idx_keyword_value", columnList = "value"),@Index(name = "idx_keyword_member", columnList = "member"),
        @Index(name = "idx_keyword_team", columnList = "team")})
public class Keyword {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String value;

    @OneToOne
    @JoinColumn(name="team")
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member")
    @JsonIgnore
    private Member member;

}
