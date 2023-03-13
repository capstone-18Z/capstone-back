package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String detail;
}
