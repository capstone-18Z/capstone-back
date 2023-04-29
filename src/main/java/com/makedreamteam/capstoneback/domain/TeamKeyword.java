package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(15) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String category;

    @Column(columnDefinition = "VARCHAR(15) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String field;

    @Column(columnDefinition = "VARCHAR(4) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String sub="none";

    @OneToOne
    @JoinColumn(name="team")
    private Team team;
}
