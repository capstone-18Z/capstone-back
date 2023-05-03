package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;

    private String title;
    private String url;
    private String host;
    private String target;
    private String dday;
    private String state;
    @Column(columnDefinition = "LONGTEXT")
    private String imgUrl;
    private String period;
    private String AuditDate;
    private String releaseDate;
}
