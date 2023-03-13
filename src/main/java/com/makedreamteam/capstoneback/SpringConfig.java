package com.makedreamteam.capstoneback;


import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    private final SpringDataTeamRepository springDataTeamRepository;
}
