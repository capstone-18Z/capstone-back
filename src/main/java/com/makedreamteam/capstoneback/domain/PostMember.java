package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String nickname;

    @Column
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String detail;

    @OneToMany(mappedBy = "postMember", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @JsonProperty("memberKeywords")
    public List<String> getKeywordValues() {
        return memberKeywords.stream().map(MemberKeyword::getValue).collect(Collectors.toList());
    }

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileData> fileDataList = new ArrayList<>();

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
