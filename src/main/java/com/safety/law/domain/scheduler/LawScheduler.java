package com.safety.law.domain.scheduler;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.safety.law.domain.scheduler.service.LawSchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
// @Profile("local")
public class LawScheduler {

    private final LawSchedulerService lawSchedulerService;

    @Scheduled(cron = "0 0,6 * * * ?")
    // @Scheduled(fixedDelay = 1 * 1000 * 60 * 3)
    public void lawDataScheduler() throws Exception{
        
        lawSchedulerService.lawDataScheduler();
    }

}
