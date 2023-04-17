package com.makedreamteam.capstoneback.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(indexes = {@Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_nickname", columnList = "nickname")})
public class Member {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @PrePersist
    public void createUserUniqId() {
        //sequential uuid 생성
        UUID uuid = Generators.timeBasedGenerator().generate();
        String[] uuidArr = uuid.toString().split("-");
        String uuidStr = uuidArr[2]+uuidArr[1]+uuidArr[0]+uuidArr[3]+uuidArr[4];
        StringBuffer sb = new StringBuffer(uuidStr);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        uuid = UUID.fromString(sb.toString());
        this.id = uuid;
    }

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

    @Column
    private String github;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
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
