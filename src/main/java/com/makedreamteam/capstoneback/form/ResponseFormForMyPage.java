package com.makedreamteam.capstoneback.form;

import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingUserToTeam;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResponseFormForMyPage {
    //내가만든 모든 팀 리스트
    private List<Team> myAllTeams;
    //내가 작성한 유저 포스트
    private List<PostMember> myAllPost;
    //내가 팀원 신청한 리스트
    private List<WaitingListOfMatchingUserToTeam> ListOfRequestISubmitted;
}
