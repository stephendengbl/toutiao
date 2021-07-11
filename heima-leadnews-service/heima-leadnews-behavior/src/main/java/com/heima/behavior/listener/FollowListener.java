package com.heima.behavior.listener;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.dto.FollowBehaviorDto;
import com.heima.behavior.service.IApFollowBehaviorService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FollowListener {

    @Autowired
    private IApFollowBehaviorService followBehaviorService;

    // 指定监听器 指定需要监听的topic主题
    @KafkaListener(topics = "${topic.followBehaviorTopic}")
    public void handleMsg(ConsumerRecord<String, String> record) {
        // 获取到消息
        String value = record.value();
        if (!StringUtils.isEmpty(value)) {
            // Json转换成为对象
            FollowBehaviorDto followBehaviorDto = JSON.parseObject(value, FollowBehaviorDto.class);
            // 做关注行为的保存
            followBehaviorService.saveBehavior(followBehaviorDto);
        }
    }
}
