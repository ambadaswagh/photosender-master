package com.cwidanage.photoftp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/**
 * @author Chathura Widanage
 */
@Service
@EnableScheduling
@EnableAsync
public class LicenceService {

    Logger logger = LogManager.getLogger(LicenceService.class);

    @Scheduled(fixedDelay = 100000)
    public void check() {
        
    }
}
