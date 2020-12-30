package com.studyolle.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        /*
        * JVM (Java Virtual Machine)에서 사용할 수있는 프로세서 수를 반환합니다.
        * 이 값은 가상 머신의 특정 호출 중에 변경 될 수 있습니다.
        *  따라서 사용 가능한 프로세서 수에 민감한 응용 프로그램은 때때로이 속성을 폴링하고 리소스 사용량을 적절하게 조정해야합니다.
        * */
        int processors = Runtime.getRuntime().availableProcessors();
        log.info("processor count {} ", processors);
        // pool크기 설정
        executor.setCorePoolSize(processors);
        // 최대 pool크기(blockingQueue 가득찼을 때 수용할 최대 크기)
        executor.setMaxPoolSize(processors * 2);
        //BlockingQueue용량(기본 값은 Interger.MAX_VALUE)
        executor.setQueueCapacity(50);        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor~");
        executor.initialize();

        return executor;
    }
}
