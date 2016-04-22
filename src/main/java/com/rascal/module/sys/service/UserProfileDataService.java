package com.rascal.module.sys.service;

import com.google.common.collect.Maps;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.dao.UserProfileDataDao;
import com.rascal.module.sys.entity.UserProfileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserProfileDataService extends BaseService<UserProfileData, Long> {

    @Autowired
    private UserProfileDataDao userProfileDataDao;

    @Override
    protected BaseDao<UserProfileData, Long> getEntityDao() {
        return userProfileDataDao;
    }

    @Transactional(readOnly = true)
    public UserProfileData findByUserAndCode(User user, String code) {
        return userProfileDataDao.findByUserAndCode(user, code);
    }

    @Transactional(readOnly = true)
    public List<UserProfileData> findByUser(User user) {
        return userProfileDataDao.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findMapDataByUser(User user) {
        Map<String, Object> mapData = Maps.newHashMap();
        for (UserProfileData data : userProfileDataDao.findByUser(user)) {
            mapData.put(data.getCode(), data.getValue());
        }
        return mapData;
    }
}
