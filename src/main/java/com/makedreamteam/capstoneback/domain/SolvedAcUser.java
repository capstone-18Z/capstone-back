package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SolvedAcUser {
    private String handle;
    private String bio;
    private String badgeId;
    private String backgroundId;
    private String profileImageUrl;
    private int solvedCount;
    private int voteCount;
    private long exp;
    private int tier;
    private int rating;
    private int ratingByProblemsSum;
    private int ratingByClass;
    private int ratingBySolvedCount;
    private int ratingByVoteCount;
    private int classValue;
    private String classDecoration;
    private int rivalCount;
    private int reverseRivalCount;
    private int maxStreak;
    private int coins;
    private int stardusts;
    private String joinedAt;
    private String bannedUntil;
    private String proUntil;
    private int rank;
    private String error;
}
