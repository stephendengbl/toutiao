package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.heima.article.entity.ApArticle;
import com.heima.article.service.IApArticleService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

@Service
public class UpDownListener {

    @Autowired
    private IApArticleService articleService;

    // 添加监听器 指定监听的主题
    @KafkaListener(topics = "${topic.upDownTopic}")
    public void handleMessage(ConsumerRecord<String, String> record) {
        // 分析结果 ,获取到value
        String value = record.value();
        if (!StringUtils.isEmpty(value)) {
            HashMap map = JSON.parseObject(value, HashMap.class);
            // 获取文章id
            Long id = (Long) map.get("id");
            // 获取需要更新的状态
            Boolean isDown = (Boolean) map.get("isDown");
            // 调用服务接口更新文章状态
            LambdaUpdateWrapper<ApArticle> update = new LambdaUpdateWrapper<>();
            update.eq(ApArticle::getId, id);
            update.set(ApArticle::getIsDown, isDown);
            articleService.update(update);
        }

    }
}
