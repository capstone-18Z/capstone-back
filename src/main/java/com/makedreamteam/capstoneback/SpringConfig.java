package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import com.makedreamteam.capstoneback.service.TeamMemberService;
import com.makedreamteam.capstoneback.repository.UserRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import com.makedreamteam.capstoneback.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringConfig implements WebMvcConfigurer {
    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";
   /* private final PostTeamRepository postTeamRepository;



    public SpringConfig(PostTeamRepository postTeamRepository) {
        this.postTeamRepository = postTeamRepository;

    }*/
public class SpringConfig {
    private final UserRepository userRepository;
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    private final SpringDataTeamRepository springDataTeamRepository;
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;
    private final TeamMemberRepository teamMemberRepository;


    public SpringConfig(SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository, SpringDataJpaUserLangRepository springDataJpaUserLangRepository, TeamMemberRepository teamMemberRepository) {
    public SpringConfig(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository, SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository,
                        SpringDataTeamRepository springDataTeamRepository, UserRepository userRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
        this.userRepository = userRepository;

        this.springDataJpaUserLangRepository = springDataJpaUserLangRepository;
        this.teamMemberRepository = teamMemberRepository;
    }


    @Bean
    public TeamService TeamService(){
        return new TeamService(springDataJpaTeamRepository, springDataJpaTeamLangRepository, springDataTeamRepository);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }

}
