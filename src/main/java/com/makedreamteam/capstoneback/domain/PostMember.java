package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="post_member")
public class PostMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @Column
    private UUID userId;
    @Column
    private String nickname;
    @Column
    private String title;
    @Column
    private String detail;
    @Column
    private int field;//1 : 프론트  2 : 백 ,  3 : 구분없음
    @OneToMany(mappedBy = "postMember", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @JsonProperty("memberKeywords")
    public List<String> getKeywordValues() {
        return memberKeywords.stream().map(MemberKeyword::getValue).collect(Collectors.toList());
    }

    public void addKeyword(MemberKeyword memberKeywords) {
        this.memberKeywords.add(memberKeywords);
        memberKeywords.setPostMember(this);
    }

    public void removeKeyword(MemberKeyword memberKeywords) {
        this.memberKeywords.remove(memberKeywords);
        memberKeywords.setPostMember(null);
    }

    public void setTeam(MemberKeyword memberKeywords){
        memberKeywords.setPostMember(this);
    }
}
