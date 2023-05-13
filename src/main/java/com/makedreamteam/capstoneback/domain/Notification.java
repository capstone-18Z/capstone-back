package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private UUID user;

    @Column
    private String msg;
}
