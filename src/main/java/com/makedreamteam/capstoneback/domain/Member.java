package com.makedreamteam.capstoneback.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Member {
    @Id
    @Column(name = "member_id", columnDefinition = "BINARY(16) ")
    @GeneratedValue
    private UUID id;

    @Column(length = 100, unique = true, nullable = false, columnDefinition="VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String email;
    private String password;

    @Column(unique = true, nullable = false , columnDefinition="VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @ColumnDefault("0")
    private int python;

    @Column
    @ColumnDefault("0")
    private int c;

    @Column
    @ColumnDefault("0")
    private int java;

    @Column
    @ColumnDefault("0")
    private int cpp;

    @Column
    @ColumnDefault("0")
    private int cs;

    @Column
    @ColumnDefault("0")
    private int vb;

    @Column
    @ColumnDefault("0")
    private int javascript;

    @Column
    @ColumnDefault("0")
    private int assembly;

    @Column
    @ColumnDefault("0")
    private int php;

    @Column
    @ColumnDefault("0")
    private int sqllang;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PostMember> postMemberList = new ArrayList<>();


    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
