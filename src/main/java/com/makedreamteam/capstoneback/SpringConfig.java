package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringConfig implements WebMvcConfigurer {
    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";
   /* private final PostTeamRepository postTeamRepository;
    public SpringConfig(PostTeamRepository postTeamRepository) {
        this.postTeamRepository = postTeamRepository;

    }*/
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    private final SpringDataTeamRepository springDataTeamRepository;
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;
    private final TeamMemberRepository teamMemberRepository;

       public SpringConfig(SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository, SpringDataJpaUserLangRepository springDataJpaUserLangRepository, TeamMemberRepository teamMemberRepository) {
           this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
           this.springDataTeamRepository = springDataTeamRepository;
           this.springDataJpaUserLangRepository = springDataJpaUserLangRepository;
           this.teamMemberRepository = teamMemberRepository;
       }

       @Bean
    public TeamService TeamService(){
        return new TeamService(springDataJpaTeamLangRepository,springDataTeamRepository,springDataJpaUserLangRepository);
    }


    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }

}
