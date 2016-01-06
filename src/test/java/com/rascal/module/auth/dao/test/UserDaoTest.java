package com.rascal.module.auth.dao.test;

import com.rascal.core.util.MockEntityUtils;
import com.rascal.module.auth.dao.UserDao;
import com.rascal.module.auth.entity.User;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserDaoTest extends SpringTransactionalTestCase {

    @Qualifier("userDao")
    @Autowired
    private UserDao userDao;

    @Test
    public void findByAuthTypeAndAuthUid() {
        User entity = MockEntityUtils.buildMockObject(User.class);
        entity.setEmail("123@abc.com");
        userDao.save(entity);

        logger.debug("1 Find before cache...");
        userDao.findOne(entity.getId()).getAccessToken();
        logger.debug("2 Find after cache...");
        userDao.findOne(entity.getId()).getAccessToken();

        logger.debug("3 Query before cache...");
        userDao.findByAuthTypeAndAuthUid(entity.getAuthType(), entity.getAuthUid()).getAccessToken();
        logger.debug("4 Query after cache...");
        userDao.findByAuthTypeAndAuthUid(entity.getAuthType(), entity.getAuthUid()).getAccessToken();
    }
}
