package com.heima.user.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestTask {

    @Async
    public void handleSom() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("子任务执行完成,当前线程为: " + Thread.currentThread().getName());
    }
}
