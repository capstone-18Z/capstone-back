package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

   /* private final PostTeamRepository postTeamRepository;



    public SpringConfig(PostTeamRepository postTeamRepository) {
        this.postTeamRepository = postTeamRepository;

    }*/
    private final SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository;
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    private final SpringDataTeamRepository springDataTeamRepository;

    public SpringConfig(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository, SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
    }


    @Bean
    public TeamService TeamService(){
        return new TeamService(springDataJpaPostTeamRepository, springDataJpaTeamLangRepository, springDataTeamRepository);
    }



}
