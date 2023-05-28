package com.makedreamteam.capstoneback.domain;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingListOfMatchingTeamToUser {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID Id;

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
        this.Id = uuid;
    }

    @Column
    private UUID memberId;

    @Column
    private UUID teamId;

    @Column
    private int field;
}
