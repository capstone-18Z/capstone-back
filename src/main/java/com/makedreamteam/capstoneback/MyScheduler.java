package com.makedreamteam.capstoneback;

import com.makedreamteam.capstoneback.repository.ContestRepository;
import com.makedreamteam.capstoneback.service.ContestCrawlingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyScheduler {
    @Autowired
    private final ContestCrawlingService contestCrawlingService;
    @Autowired
    private final ContestRepository contestRepository;

    @Scheduled(cron = "3 0 0 * * ?") // 매일 0시 0분에 실행
    @Transactional
    public void myTask() {
        contestRepository.deleteAll();
        contestCrawlingService.crawlContest();
    }
}
