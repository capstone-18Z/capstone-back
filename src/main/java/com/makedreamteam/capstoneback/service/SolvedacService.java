package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.SolvedAcUser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class SolvedacService {
    private final RestTemplate restTemplate;

    public SolvedacService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public SolvedAcUser getUser(String username) {
        String url = String.format("https://solved.ac/api/v3/user/show?handle=%s", username);
        System.out.println(url);
        ResponseEntity<SolvedAcUser> response = restTemplate.getForEntity(url, SolvedAcUser.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get user information from SolvedAc API");
        }
    }
}
