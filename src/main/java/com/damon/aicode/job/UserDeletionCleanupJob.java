package com.damon.aicode.job;

import com.damon.aicode.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionCleanupJob {

    private final UserService userService;

    @Scheduled(cron = "0 0 * * * ?")
    public void purgeExpiredSelfDeletedUsers() {
        long deletedCount = userService.purgeExpiredSelfDeletedUsers();
        if (deletedCount > 0) {
            log.info("Purged {} expired self-deleted users", deletedCount);
        }
    }
}
