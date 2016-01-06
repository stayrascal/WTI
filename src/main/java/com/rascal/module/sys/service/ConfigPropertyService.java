package com.rascal.module.sys.service;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.module.sys.dao.ConfigPropertyDao;
import com.rascal.module.sys.entity.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConfigPropertyService extends BaseService<ConfigProperty, Long> {

    @Autowired
    private ConfigPropertyDao configPropertyDao;

    public List<ConfigProperty> findAllCached() {
        return configPropertyDao.findAllCached();
    }

    @Override
    protected BaseDao<ConfigProperty, Long> getEntityDao() {
        return configPropertyDao;
    }

    @Transactional(readOnly = true)
    public ConfigProperty findByPropKey(String propKey) {
        return configPropertyDao.findByPropKey(propKey);
    }

    @Transactional(readOnly = true)
    public String findValueByPropKey(String propKey) {
        ConfigProperty configProperty = configPropertyDao.findByPropKey(propKey);
        if (configProperty != null) {
            return configProperty.getSimpleValue();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String findHtmlByPropKey(String propKey) {
        ConfigProperty configProperty = configPropertyDao.findByPropKey(propKey);
        if (configProperty != null) {
            return configProperty.getHtmlValue();
        }
        return null;
    }
}
