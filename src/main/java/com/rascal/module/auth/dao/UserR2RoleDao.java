package com.rascal.module.auth.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.UserR2Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserR2RoleDao extends BaseDao<UserR2Role, String> {

    @Query("select r2 from Role r, UserR2Role r2 where r=r2.role and r2.user.id=:userId and r.disabled=false ")
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<UserR2Role> findEnabledRolesForUser(@Param("userId") Long userId);

    List<UserR2Role> findByRole_Id(String roleId);

    @Query("select r2 from User u, UserR2Role r2, Role r where r=r2.role and u=r2.user and r2.user=:user order by r.code")
    List<UserR2Role> findByUser(@Param("user") User user);
}
