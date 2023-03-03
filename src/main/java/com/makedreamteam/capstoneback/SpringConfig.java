package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    public SpringConfig( SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository) {
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
    }


    @Bean
    public TeamService TeamService(){
        return new TeamService(springDataJpaTeamLangRepository, springDataTeamRepository);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }

}
