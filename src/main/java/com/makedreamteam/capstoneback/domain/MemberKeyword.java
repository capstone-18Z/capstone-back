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
@Table(indexes = {@Index(name = "idx_member_keyword_value", columnList = "value"),
        @Index(name = "idx_member_keyword_member", columnList = "member_id")})
@NoArgsConstructor
public class MemberKeyword {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JsonIgnore
    private UUID id;

    @Column(columnDefinition = "VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

}
