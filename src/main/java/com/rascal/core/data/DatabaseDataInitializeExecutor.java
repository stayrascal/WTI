package com.rascal.core.data;

import com.google.common.collect.Sets;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.util.DateUtils;
import com.rascal.support.service.DynamicConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.ClassUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.DatabaseMetaData;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 数据库数据初始化处理器触发器
 */
@Component
@Transactional
public class DatabaseDataInitializeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDataInitializeExecutor.class);

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Value("hibernate_hbm2ddl_auto:")
    private String hbm2ddl;

    /**
     * 初始化自增对象起始值
     */
    public static void autoIncrementInitValue(final Class<?> entity, final EntityManager entityManager) {
        Object count = entityManager.createQuery(String.format("select count(1) from %s", entity.getSimpleName())).getSingleResult();
        if (Integer.valueOf(String.valueOf(count)) > 0) {
            logger.debug("Skipped autoIncrementInitValue as exist data: {}", entity.getClass());
            return;
        }
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            Table table = entity.getAnnotation(Table.class);
            MetaData metaData = entity.getAnnotation(MetaData.class);
            Assert.isTrue(metaData.autoIncrementInitValue() > 1, "Undefined MetaData autoIncrementInitValue for entity: " + entity.getClass());

            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String name = databaseMetaData.getDatabaseProductName().toLowerCase();
            //根据不同数据库类型执行不同初始化SQL脚本
            String sql = null;
            if (name.contains("mysql")) {
                sql = String.format("ALTER TABLE %s AUTO_INCREMENT =%d", table.name(), metaData.autoIncrementInitValue());
            } else if (name.contains("sql server")) {
                //DBCC   CHECKIDENT( 'tb ',   RESEED,   20000)
                sql = String.format("DBCC CHECKIDENT('%s',RESEED,%d)", table.name(), metaData.autoIncrementInitValue());
            } else if (name.contains("h2")) {
                //DO Nothing;
            } else if (name.contains("oracle")) {
                //DO Nothing;
            } else {
                throw new UnsupportedOperationException(name);
            }

            if (StringUtils.isNotBlank(sql)) {
                logger.debug("Execute autoIncrementInitValue SQL: {}", sql);
                entityManager.createNativeQuery(sql).executeUpdate();
            }
        });
    }

    @Transactional
    public void initialize(List<BaseDatabaseDataInitialize> initializeProcessors) {
        CountThread countThread = new CountThread();
        countThread.start();

        //必须是开发模式才允许删除数据库
        if (DynamicConfigService.isDevMode() && "create-drop".equalsIgnoreCase(hbm2ddl)) {
            logger.debug("Invoke hibernate sessionFactory.close() to trigger drop tables as hbm2ddl={}.", hbm2ddl);
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            sessionFactory.close();
            logger.debug("Hibernate hbm2ddl drop done, force System.exit()");
            System.exit(0);
        }

        {
            //搜索所有entity对象，并自动进行自增初始化值设置
            Set<BeanDefinition> beanDefinitions = Sets.newHashSet();
            ClassPathScanningCandidateComponentProvider scan = new ClassPathScanningCandidateComponentProvider(false);
            scan.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
            beanDefinitions.addAll(scan.findCandidateComponents("com.rascal.**.entity.**"));

            for (BeanDefinition beanDefinition : beanDefinitions) {
                Class<?> entityClass = ClassUtils.forName(beanDefinition.getBeanClassName());
                MetaData metaData = entityClass.getAnnotation(MetaData.class);
                if (metaData != null && metaData.autoIncrementInitValue() > 0) {
                    autoIncrementInitValue(entityClass, entityManager);
                }
            }
        }

        for (BaseDatabaseDataInitialize initializeProcessor : initializeProcessors) {
            logger.debug("Invoking data initialize for {}", initializeProcessor);
            countThread.update(initializeProcessor.getClass());
            initializeProcessor.initialize(entityManager);
        }

        //停止计数线程
        countThread.shutdown();
        //清空释放所有基础和模拟数据操作缓存
        entityManager.clear();
    }

    private static class CountThread extends Thread {

        private Class<?> runnerClass;
        private boolean running = true;
        private Date startTime = new Date();

        @Override
        public void run() {
            while (running) {
                try {
                    if (runnerClass != null) {
                        Date now = new Date();
                        logger.info("Running at " + runnerClass.getName() + ". Total passed time "
                                + DateUtils.getHumanDisplayForTimediff(now.getTime() - startTime.getTime()) + " at Thread: "
                                + Thread.currentThread().getId());
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            super.run();
        }

        public void update(Class<?> runnerClass) {
            this.runnerClass = runnerClass;
        }

        public void shutdown() {
            logger.info("Shutdowning DatabaseDataInitializeTrigger CountThread: " + Thread.currentThread().getId());
            running = false;
        }
    }
}
