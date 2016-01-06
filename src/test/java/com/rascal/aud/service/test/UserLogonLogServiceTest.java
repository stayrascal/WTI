package com.rascal.aud.service.test;

import com.rascal.aud.service.UserLogonLogService;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserLogonLogServiceTest extends SpringTransactionalTestCase {

    @Autowired
    private UserLogonLogService userLogonLogService;

    @Test
    public void findGroupByLogonDay() {
        List<Map<String, Object>> items = userLogonLogService.findGroupByLogonDay();
        for (Map<String, Object> item : items) {
            logger.debug("Item: {}", item);
        }
    }
}
