package com.rascal.module.sys.service.test;

import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.service.NotifyMessageService;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotifyMessageServiceTest extends SpringTransactionalTestCase {

    @Autowired
    private NotifyMessageService notifyMessageService;

    @Test
    public void findSiteCountToRead() {
        User user = new User();
        user.setId(1L);
        Long count = notifyMessageService.findCountToRead(user, "web-admin");
        logger.debug("findSiteCountToRead Count: {}", count);
    }
}
