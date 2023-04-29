package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostMember post;

    @Column(columnDefinition = "LONGTEXT")
    private String fileName;

    @Column(name = "filetype")
    private FileType fileType;

    @Column(columnDefinition = "LONGTEXT")
    private String originalName;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column
    private String imageURL;
}
