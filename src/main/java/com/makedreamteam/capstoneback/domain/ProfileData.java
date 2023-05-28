package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ProfileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "filetype")
    private FileType fileType;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column
    private String imageURL;
}
