package com.makedreamteam.capstoneback.repository;

import java.util.*;

import com.makedreamteam.capstoneback.domain.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(UUID id);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    @Override
    List<Member> findAll();

    @Query("SELECT mk.value AS member_keyword, tk.value AS team_keyword " +
            "FROM Member m " +
            "JOIN MemberKeyword mk ON m.id = mk.id " +
            "LEFT JOIN Team t " +
            "LEFT JOIN TeamKeyword tk ON t.teamId = tk.team " +
            "WHERE mk.value = tk.value")
    List<Object[]> findMemberKeywordsWithTeamKeywords();
    @Query("SELECT  tk.team.teamId,mk.member.id FROM MemberKeyword mk JOIN TeamKeyword tk ON mk.value = tk.value")
    List<Object[]> findMemberAndTeamKeywordValues();
    default Map<UUID, List<UUID>> getMemberAndTeamKeywordValues() {
        List<Object[]> result = findMemberAndTeamKeywordValues();
        Map<UUID, List<UUID>> resultMap = new HashMap<>();

        for (Object[] obj : result) {
            UUID memberId = (UUID) obj[0];
            UUID teamId = (UUID) obj[1];

            if (!resultMap.containsKey(memberId)) {
                resultMap.put(memberId, new ArrayList<>());
            }
            resultMap.get(memberId).add(teamId);
        }

        return resultMap;
    }
}
