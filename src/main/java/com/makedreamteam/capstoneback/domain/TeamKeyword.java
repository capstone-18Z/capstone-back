package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamKeyword {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team")
    @JsonIgnore
    private Team team;




}
