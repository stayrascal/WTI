package com.rascal.module.auth.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.SignupUser;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface SignupUserDao extends BaseDao<SignupUser, Long> {
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    SignupUser findByAuthUid(String authUid);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<SignupUser> findByEmail(String email);

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<SignupUser> findByMobile(String mobile);
}