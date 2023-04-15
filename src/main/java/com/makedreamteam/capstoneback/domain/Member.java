package com.makedreamteam.capstoneback.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"member"})
    private MemberLang memberLang;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"member"})
    private MemberFramework memberFramework;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"member"})
    private MemberDatabase memberDB;

    @Column(length = 100, unique = true, nullable = false, columnDefinition="VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String email;
    private String password;

    @Column(unique = true, nullable = false , columnDefinition="VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String profileImageUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PostMember> postMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    public void addKeyword(MemberKeyword memberKeywords) {
        this.memberKeywords.add(memberKeywords);
        memberKeywords.setMember(this);
    }

    public void removeKeyword(MemberKeyword memberKeywords) {
        this.memberKeywords.remove(memberKeywords);
        memberKeywords.setMember(null);
    }
    public void setTeam(MemberKeyword memberKeywords){
        memberKeywords.setMember(this);
    }

    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
