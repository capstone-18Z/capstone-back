package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.MemberKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@Repository
public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, UUID> {
    @Query("SELECT m from Member m JOIN  m.memberKeywords k where m.nickname like %:search% AND ((k.field IN :rule and k.category IN :category) OR k.field IN :subject)")
    Page<Member> findAllByFilter(@Param("category") List<String> category, @Param("subject") List<String> subject, @Param("rule") List<String> rule,@Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE m.nickname like %:search% AND (k.field IN :rule  OR k.field IN :subject )")
    Page<Member> findAllByFilterWithoutCategory(@Param("subject") List<String> subject, @Param("rule") List<String> rule, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE m.nickname like %:search% AND (k.category IN :category OR k.field IN :subject)")
    Page<Member> findAllByFilterWithoutRule( @Param("category") List<String> category,@Param("subject") List<String> subject, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE m.nickname like %:search% AND  k.field IN :subject")
    Page<Member> findAllByFilterWithoutCategoryAndRule( @Param("subject") List<String> subject, @Param("search") String search, Pageable pageable);
    @Query("select member from MemberKeyword  where (field in :rule and category in :category) or field in :subject")
    Page<Member> findAllByFilterWithoutSearch(@Param("category") List<String> category, @Param("subject") List<String> subject, @Param("rule") List<String> rule, Pageable pageable);


    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE k.field IN :rule  OR k.field IN :subject")
    Page<Member> findAllByFilterWithoutCategoryAndSearch( @Param("subject") List<String> subject, @Param("rule") List<String> rule, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE k.category IN :category OR k.field IN :subject ")
    Page<Member> findAllByFilterWithoutRuleAndSearch( @Param("category") List<String> category,@Param("subject") List<String> subject, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.memberKeywords k WHERE k.field IN :subject")
    Page<Member> findAllByFilterWithoutCategoryAndRuleAndSearch( @Param("subject") List<String> subject,  Pageable pageable);

}
