package com.makedreamteam.capstoneback.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(indexes = {@Index(name = "idx_title", columnList = "title")})//게시물 검색 시 제목을 주로 검색을 하므로 title을 인덱싱한다
public class Team{
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID teamId;

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
        this.teamId = uuid;
    }

    @Column
    private UUID teamLeader;

    @Column(columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String teamName;

    @Column
    private byte wantTeamMemberCount;

    @Column
    private byte currentTeamMemberCount;

    @Column(columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String title;
    @Column(columnDefinition = "VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String writer;

    @Column
    private Date createDate;

    @Column
    private Date updateDate=new Date();

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String detail;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<WaitingListOfMatchingUserToTeam> requestList=new ArrayList<>();

    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"team"})
    private TeamKeyword teamKeyword;


    @OneToOne(mappedBy = "team",cascade = CascadeType.ALL ,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"team"})
    private TeamLanguage teamLanguage;

    @OneToOne(mappedBy = "team",cascade = CascadeType.ALL ,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"team"})
    private TeamFramework teamFramework;

    @OneToOne(mappedBy = "team",cascade = CascadeType.ALL ,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"team"})
    private TeamDatabase teamDatabase;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imagePaths;


}
