package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import com.makedreamteam.capstoneback.service.PostTeamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

   /* private final PostTeamRepository postTeamRepository;



    public SpringConfig(PostTeamRepository postTeamRepository) {
        this.postTeamRepository = postTeamRepository;

    }*/
    private final SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository;

    public SpringConfig(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
    }


    @Bean
    public PostTeamService postTeamService(){
        return new PostTeamService(springDataJpaPostTeamRepository);
    }



}
