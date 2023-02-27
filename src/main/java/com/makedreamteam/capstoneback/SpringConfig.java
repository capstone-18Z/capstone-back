package com.makedreamteam.capstoneback;

import com.makedreamteam.capstoneback.repository.UserRepository;
import com.makedreamteam.capstoneback.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final UserRepository userRepository;
    public SpringConfig(UserRepository userRepository){ this.userRepository = userRepository; }

    @Bean
    public UserService userService() {return new UserService(userRepository);}
}
