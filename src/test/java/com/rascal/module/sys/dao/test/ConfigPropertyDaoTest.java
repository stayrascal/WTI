package com.rascal.module.sys.dao.test;

import com.rascal.core.util.MockEntityUtils;
import com.rascal.module.sys.dao.ConfigPropertyDao;
import com.rascal.module.sys.entity.ConfigProperty;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfigPropertyDaoTest extends SpringTransactionalTestCase {

    @Qualifier("configPropertyDao")
    @Autowired
    private ConfigPropertyDao configPropertyDao;

    @Test
    public void save() {
        ConfigProperty entity = MockEntityUtils.buildMockObject(ConfigProperty.class);
        configPropertyDao.save(entity);
        configPropertyDao.findAll();
    }
}
