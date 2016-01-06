package com.rascal.module.auth.service;

import com.google.common.collect.Lists;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.module.auth.dao.DepartmentDao;
import com.rascal.module.auth.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class DepartmentService extends BaseService<Department, Long> {

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    protected BaseDao<Department, Long> getEntityDao() {
        return departmentDao;
    }

    @Transactional(readOnly = true)
    public List<Department> findRoots() {
        List<Department> roots = Lists.newArrayList();
        Iterable<Department> items = departmentDao.findAllCached();
        for (Department item : items) {
            if (item.getParent() == null) {
                roots.add(item);
            }
        }
        Collections.sort(roots);
        return roots;
    }

    @Transactional(readOnly = true)
    public List<Department> findChildren(Department parent) {
        if (parent == null) {
            return findRoots();
        }
        List<Department> children = Lists.newArrayList();
        Iterable<Department> items = departmentDao.findAllCached();
        for (Department item : items) {
            if (parent.equals(item.getParent())) {
                children.add(item);
            }
        }
        Collections.sort(children);
        return children;
    }
}
