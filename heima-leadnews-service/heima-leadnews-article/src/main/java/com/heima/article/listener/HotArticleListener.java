package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.dto.ArticleStreamMessage;
import com.heima.article.dto.UpdateArticleMessage;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.util.StringUtils;

import java.time.Duration;

@EnableBinding(value = HotArticleListener.HotArticleProcessor.class)
public class HotArticleListener {
    // 监听主题和发送结果主题
    @StreamListener("hot_article_score_topic")
    @SendTo("hot_article_result_topic")
    public KStream<String, String> process(KStream<String, String> input) {
        // 从流中获取数据,进行转换
        // UpdateArticleMessage   {"articleId":1397400822379196418,"type":1,"add":1}

        // 需要根据文章id统计一段时间内的所有操作 结果 ArticleStreamMessage

        KStream<String, String> map = input.map(new KeyValueMapper<String, String, KeyValue<String, String>>() {
            @Override
            public KeyValue<String, String> apply(String key, String value) {
                // 从JSON转换称为对象
                UpdateArticleMessage updateArticleMessage = JSON.parseObject(value, UpdateArticleMessage.class);
                Long articleId = updateArticleMessage.getArticleId();
                return new KeyValue<>(articleId.toString(), value);
            }
        });
        // 根据key来分组
        KGroupedStream<String, String> groupByKey = map.groupByKey(Grouped.with(Serdes.String(), Serdes.String()));
        // 根据时间窗口来统计
        TimeWindowedKStream<String, String> windowedKStream = groupByKey.windowedBy(TimeWindows.of(Duration.ofSeconds(10)));
        // 进行聚合的运算
        // 运算最开始的结果
        Initializer<String> init = new Initializer<String>() {
            @Override
            public String apply() {
                return null;
            }
        };
        // 定义聚合运算
        Aggregator<String, String, String> agg = new Aggregator<String, String, String>() {
            @Override
            public String apply(String key, String value, String aggregate) {
                // key  --> 分组的key
                // value --> 最初传递进来的UpdateArticleMessage   {"articleId":1397400822379196418,"type":1,"add":1}
                // aggregate  --> 上一次聚合的结果
                long articleId = Long.parseLong(key);
                UpdateArticleMessage updateArticleMessage = JSON.parseObject(value, UpdateArticleMessage.class);
                // 构建最终的结果 ArticleStreamMessage
                ArticleStreamMessage result = null;
                if (StringUtils.isEmpty(aggregate)) {
                    result = new ArticleStreamMessage();
                    result.setArticleId(articleId);
                    result.setView(0);
                    result.setLike(0);
                    result.setComment(0);
                    result.setCollect(0);
                    switch (updateArticleMessage.getType()) {
                        case 0:
                            result.setView(updateArticleMessage.getAdd());
                            break;
                        case 1:
                            result.setLike(updateArticleMessage.getAdd());
                            break;
                        case 2:
                            result.setComment(updateArticleMessage.getAdd());
                            break;
                        case 3:
                            result.setCollect(updateArticleMessage.getAdd());
                            break;
                    }
                } else {
                    // 从上一次聚合的结果转换成对象
                    result = JSON.parseObject(aggregate, ArticleStreamMessage.class);
                    switch (updateArticleMessage.getType()) {
                        case 0:
                            result.setView(result.getView() + updateArticleMessage.getAdd());
                            break;
                        case 1:
                            result.setLike(result.getLike() + updateArticleMessage.getAdd());
                            break;
                        case 2:
                            result.setComment(result.getComment() + updateArticleMessage.getAdd());
                            break;
                        case 3:
                            result.setCollect(result.getCollect() + updateArticleMessage.getAdd());
                            break;
                    }
                }
                return JSON.toJSONString(result);
            }
        };
        KTable<Windowed<String>, String> aggregate = windowedKStream.aggregate(init, agg, Materialized.with(Serdes.String(), Serdes.String()));
        KStream<String, String> map1 = aggregate.toStream().map(new KeyValueMapper<Windowed<String>, String, KeyValue<String, String>>() {
            @Override
            public KeyValue<String, String> apply(Windowed<String> key, String value) {
                return new KeyValue<>(key.key(), value);
            }
        });
        return map1;
    }

    public interface HotArticleProcessor {
        @Input("hot_article_score_topic")
        KStream<String, String> input();

        @Output("hot_article_result_topic")
        KStream<String, String> output();
    }
}
