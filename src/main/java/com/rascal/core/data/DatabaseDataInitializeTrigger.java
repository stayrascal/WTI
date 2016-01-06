package com.rascal.core.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 数据库数据初始化处理器触发器
 */
public class DatabaseDataInitializeTrigger {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDataInitializeTrigger.class);

    private DatabaseDataInitializeExecutor databaseDataInitializeExecutor;

    private List<BaseDatabaseDataInitialize> initializeProcessors;

    public void initialize() {
        try {
            databaseDataInitializeExecutor.initialize(initializeProcessors);
        } catch (Exception e) {
            String msg = null;
            Throwable msgException = e;
            do {
                msg = msgException.getMessage();
                msgException = msgException.getCause();
            } while (msgException != null);

            if (msg != null && msg.contains("Transaction not active")) {
                logger.warn(msg);
            } else {
                logger.warn(e.getMessage(), e);
            }
        }

    }

    public void setInitializeProcessors(List<BaseDatabaseDataInitialize> initializeProcessors) {
        this.initializeProcessors = initializeProcessors;
    }

    public void setDatabaseDataInitializeExecutor(DatabaseDataInitializeExecutor databaseDataInitializeExecutor) {
        this.databaseDataInitializeExecutor = databaseDataInitializeExecutor;
    }
}
