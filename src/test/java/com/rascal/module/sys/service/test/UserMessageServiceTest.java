package com.rascal.module.sys.service.test;

import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.service.UserMessageService;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMessageServiceTest extends SpringTransactionalTestCase {

    @Autowired
    private UserMessageService userMessageService;

    @Test
    public void findSiteCountToRead() {
        User user = new User();
        user.setId(1L);
        Long count = userMessageService.findCountToRead(user);
        logger.debug("findSiteCountToRead Count: {}", count);
    }

}
