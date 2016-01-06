package com.rascal.module.auth.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.User.AuthTypeEnum;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserDao extends BaseDao<User, Long> {

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    User findByAuthTypeAndAuthUid(AuthTypeEnum authType, String authUid);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    User findByAuthTypeAndAccessToken(AuthTypeEnum authType, String accessToken);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    User findByAuthUid(String authUid);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<User> findByEmail(String email);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<User> findByMobile(String mobile);
}