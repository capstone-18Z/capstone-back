package com.makedreamteam.capstoneback.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`room`")
    private UUID room;
    @Column
    private String msg;
    @Column(name = "`from`")
    private UUID from;
    @Column(name = "`to`")
    private UUID to;
    @Column
    private String mode;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

}
