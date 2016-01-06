package com.rascal.module.sys.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.entity.UserProfileData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileDataDao extends BaseDao<UserProfileData, Long> {

    UserProfileData findByUserAndCode(User user, String code);

    List<UserProfileData> findByUser(User user);
}