package com.camellya.gbt32960_3_tcp_server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 核心线程数，核心线程会一直存在，即使它们处于空闲状态
        executor.setMaxPoolSize(20);   // 最大线程数，当核心线程用完，且任务队列已满时，会创建的最大线程数
        executor.setQueueCapacity(200);  // 任务队列容量
        executor.setThreadNamePrefix("TCP-TASK-");  // 线程名前缀，方便在日志中定位异步线程
        executor.setKeepAliveSeconds(10);  // 线程空闲时间，超时后会被销毁
        executor.setWaitForTasksToCompleteOnShutdown(true);  // 等待所有任务完成再关闭线程池
        executor.initialize();  // 初始化线程池
        return executor;
    }
}