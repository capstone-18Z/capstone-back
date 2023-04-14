package com.makedreamteam.capstoneback.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @OneToOne(mappedBy = "member")
    private MemberLang memberLang;

    @OneToOne(mappedBy = "member")
    private MemberFramework memberFramework;

    @OneToOne(mappedBy = "member")
    private MemberDB memberDB;

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

    @OneToMany(mappedBy = "postMember", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @JsonProperty("memberKeywords")
    public List<String> getKeywordValues() {
        return memberKeywords.stream().map(MemberKeyword::getValue).collect(Collectors.toList());
    }

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
