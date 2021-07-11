package com.heima.admin.listener;

import com.heima.admin.service.IAuditService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuditListener {

    @Autowired
    private IAuditService auditService;

    @KafkaListener(topics = "${topic.autoAuditTopic}")
    public void handleMessage(ConsumerRecord<String, String> record) {
        // 获取文章id 调用自动审核服务
        String value = record.value();
        if (!StringUtils.isEmpty(value)) {
            auditService.auditById(Integer.parseInt(value));
        }
    }
}
