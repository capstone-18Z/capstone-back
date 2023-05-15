package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.index.qual.SearchIndexBottom;
import org.springframework.web.socket.TextMessage;

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
    private UUID userId;

    @Column
    private String msg;


}
