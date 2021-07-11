package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.dto.ArticleStreamMessage;
import com.heima.article.service.IHotArticleService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HotArticleResultListener {

    @Autowired
    private IHotArticleService hotArticleService;

    @KafkaListener(topics = {"${topic.hotArticleResultTopic}"})
    public void handleMsg(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (!StringUtils.isEmpty(value)) {
            // 解析json
            ArticleStreamMessage articleStreamMessage = JSON.parseObject(value, ArticleStreamMessage.class);
            // 调用热点文章服务,更新数据
            hotArticleService.update(articleStreamMessage);
        }
    }
}
