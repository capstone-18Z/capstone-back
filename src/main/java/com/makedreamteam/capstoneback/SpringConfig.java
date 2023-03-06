package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.UserRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import com.makedreamteam.capstoneback.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringConfig {
    private final UserRepository userRepository;
    private final SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository;
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    private final SpringDataTeamRepository springDataTeamRepository;

    public SpringConfig(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository, SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository,
                        SpringDataTeamRepository springDataTeamRepository, UserRepository userRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
        this.userRepository = userRepository;
    }

    @Bean
    public UserService userService() {return new UserService(userRepository);}
    @Bean
    public TeamService TeamService(){
        return new TeamService(springDataJpaPostTeamRepository, springDataJpaTeamLangRepository, springDataTeamRepository);
    }

}
