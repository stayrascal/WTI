package com.rascal.core.context.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 用于增强Spring @Async 注解在异步模式能捕捉到异常日志
 * <bean id="handlingAsyncTaskExecutor" class="lab.s2jh.core.context.async.HandlingAsyncTaskExecutor"/>
 * <task:annotation-driven executor="handlingAsyncTaskExecutor" />
 */
public class HandlingAsyncTaskExecutor implements AsyncTaskExecutor {

    private final static Logger logger = LoggerFactory.getLogger(HandlingAsyncTaskExecutor.class);

    private ThreadPoolTaskExecutor executor;

    public HandlingAsyncTaskExecutor() {
        executor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        executor.setCorePoolSize(7);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(42);
        //线程池所使用的缓冲队列
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("HandlingAsyncTaskExecutor-");
        executor.initialize();
    }

    private void handle(Exception e) {
        logger.error("Async method exception", e);
    }

    @Override
    public void execute(Runnable task) {
        logger.info("the task name is {}", task.getClass().getName());
        executor.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        logger.info("the task name is {} and time is {}", task.getClass().getName(), startTimeout);
        executor.execute(createWrappedRunnable(task), startTimeout);

    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return executor.submit(createCallable(task));
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            }
        };
    }
    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                handle(e);
            }
        };
    }
}