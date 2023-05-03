package com.makedreamteam.capstoneback;

import com.makedreamteam.capstoneback.service.ContestCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyScheduler {
    @Autowired
    private final ContestCrawlingService contestCrawlingService;
    @Scheduled(cron = "0 0 0 * * ?") // 매일 0시 0분에 실행
    public void myTask() {
        contestCrawlingService.crawlContest();
    }
}
