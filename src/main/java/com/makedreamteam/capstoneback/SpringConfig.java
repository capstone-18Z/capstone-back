package com.makedreamteam.capstoneback;

import com.makedreamteam.capstoneback.repository.*;
import com.makedreamteam.capstoneback.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SpringConfig implements WebMvcConfigurer {
    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";
   /* private final PostTeamRepository postTeamRepository;
    public SpringConfig(PostTeamRepository postTeamRepository) {
        this.postTeamRepository = postTeamRepository;

    }*/

    private final SpringDataTeamRepository springDataTeamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;

    @Bean
    public TeamService TeamService(){
        return new TeamService(springDataTeamRepository, teamMemberRepository, memberRepository, postMemberRepository);
    }


    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }


}
