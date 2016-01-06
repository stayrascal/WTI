package com.rascal.shop.service;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.module.auth.entity.User;
import com.rascal.shop.dao.SiteUserDao;
import com.rascal.shop.entity.SiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SiteUserService extends BaseService<SiteUser, Long> {

    @Autowired
    private SiteUserDao siteUserDao;

    @Override
    protected BaseDao<SiteUser, Long> getEntityDao() {
        return siteUserDao;
    }

    public SiteUser findByUser(User user) {
        return siteUserDao.findByUser(user);
    }
}
