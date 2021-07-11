package com.heima.user.controller;

import com.heima.user.task.TestTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestTask testTask;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @GetMapping("/async")
    public String testAsync() {
        // 执行耗时任务
        int corePoolSize = taskExecutor.getCorePoolSize();
        System.out.println("默认的核心线程数为: " + corePoolSize);
        testTask.handleSom();
        return "执行成功,当前线程为: " + Thread.currentThread().getName();
    }
}
