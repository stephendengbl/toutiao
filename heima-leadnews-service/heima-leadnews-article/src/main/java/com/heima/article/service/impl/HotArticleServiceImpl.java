package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.heima.article.dto.ArticleStreamMessage;
import com.heima.article.entity.ApArticle;
import com.heima.article.service.IApArticleService;
import com.heima.article.service.IHotArticleService;
import com.heima.article.vo.HotArticleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotArticleServiceImpl implements IHotArticleService {

    @Autowired
    private IApArticleService articleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void compute() {
        // 1. 查询前5天的文章  根据发布时间查询  任务是1点跑的
        Date now = new Date();
        Date date = new Date(now.getYear(), now.getMonth(), now.getDate()); // 当前0:0:0
        Date before = new Date(date.getTime() - 5 * 24 * 60 * 60 * 1000);
        LambdaQueryWrapper<ApArticle> query = new LambdaQueryWrapper<>();
        query.gt(ApArticle::getPublishTime, before);
        query.lt(ApArticle::getPublishTime, date);
        List<ApArticle> articleList = articleService.list(query);

        // 2. 计算这些文章的分值  阅读1  点赞3  评论5 收藏8
        List<HotArticleVo> vos = new ArrayList<>();
        for (ApArticle apArticle : articleList) {
            HotArticleVo vo = new HotArticleVo();
            BeanUtils.copyProperties(apArticle, vo);
            int score = computeScore(apArticle);
            vo.setScore(score);
            // 计算分值
            vos.add(vo);
        }
        // 3. 为每个频道缓存30条分值最高的文章
        // 3.1 推荐首页 缓存所有文章中分值最高的30条
        vos.sort(new Comparator<HotArticleVo>() {
            @Override
            public int compare(HotArticleVo o1, HotArticleVo o2) {
                // 比较分值
                return o2.getScore() - o1.getScore();
            }
        });
        // 设置首页的key
        String homeKey = "hot_article_first_page_0";
        int size = 30;
        if (vos.size() <= 30) {
            size = vos.size();
        }
        List<HotArticleVo> hotArticleVos = vos.subList(0, size);
        redisTemplate.opsForValue().set(homeKey, JSON.toJSONString(hotArticleVos));
        // 3.2 每个频道的首页 缓存当前频道中分值最高的30条
        Map<Integer, List<HotArticleVo>> channelMap = new HashMap<>();
        for (HotArticleVo vo : vos) {
            Integer channelId = vo.getChannelId();
            if (channelMap.containsKey(channelId)) {
                // 已经有记录
                channelMap.get(channelId).add(vo);
            } else {
                List<HotArticleVo> voList = new ArrayList<>();
                voList.add(vo);
                channelMap.put(channelId, voList);
            }
        }
        // 遍历每个频道的文章
        for (Map.Entry<Integer, List<HotArticleVo>> entry : channelMap.entrySet()) {
            List<HotArticleVo> articleVoList = entry.getValue();
            // 排序  选取前30条
            articleVoList.sort(new Comparator<HotArticleVo>() {
                @Override
                public int compare(HotArticleVo o1, HotArticleVo o2) {
                    return o2.getScore() - o1.getScore();
                }
            });
            size = 30;
            if (articleVoList.size() <= 30) {
                size = articleVoList.size();
            }
            List<HotArticleVo> subList = articleVoList.subList(0, size);
            // 存放到redis中
            String channelKey = "hot_article_first_page_" + entry.getKey();
            redisTemplate.opsForValue().set(channelKey, JSON.toJSONString(subList));
        }

    }

    @Override
    public void update(ArticleStreamMessage articleStreamMessage) {

        // 根据文章id查询文章
        ApArticle article = articleService.getById(articleStreamMessage.getArticleId());
        HotArticleVo vo = new HotArticleVo();
        BeanUtils.copyProperties(article, vo);
        int score = computeScore(article);

        // 重新计算文章的分值
        // 当日分值权重整体*3
        int add = computeScore(articleStreamMessage);
        vo.setScore(score + add);
        // 更新redis中的数据
        // 查询redis中的数据,判断当前的文章id是否在查询到的列表中,如果存在,更新,并且重新排序,如果不存在,判断当前文章的分数和redis
        // 中查询的自小分数做对比,加入到队列,重新排序

        // 首页数据
        String hot_article_first_page_0 = redisTemplate.opsForValue().get("hot_article_first_page_0");
        List<HotArticleVo> hotArticleVos = JSON.parseArray(hot_article_first_page_0, HotArticleVo.class);
        // 判断当前的文章id是否在查询到的列表中
        boolean exist = false;
        for (HotArticleVo hotArticleVo : hotArticleVos) {
            if (hotArticleVo.getId().equals(articleStreamMessage.getArticleId())) {
                exist = true;
                hotArticleVo.setScore(vo.getScore());
                break;
            }
        }
        // 重新排序 放回到redis中
        hotArticleVos.sort(new Comparator<HotArticleVo>() {
            @Override
            public int compare(HotArticleVo o1, HotArticleVo o2) {
                return o2.getScore() - o1.getScore();
            }
        });
        int size = 30;
        if (hotArticleVos.size() <= 30) {
            size = hotArticleVos.size();
        }
        List<HotArticleVo> subList = hotArticleVos.subList(0, size);
        redisTemplate.opsForValue().set("hot_article_first_page_0", JSON.toJSONString(subList));

        // 每个频道的列表也需要更新  todo
        // 更新文章表的数据
        LambdaUpdateWrapper<ApArticle> update = new LambdaUpdateWrapper<>();
        update.eq(ApArticle::getId, articleStreamMessage.getArticleId());
        update.setSql("views = views +" + articleStreamMessage.getView());
        update.setSql("likes = likes +" + articleStreamMessage.getLike());
        update.setSql("comment = comment +" + articleStreamMessage.getComment());
        update.setSql("collection = collection +" + articleStreamMessage.getCollect());
        articleService.update(update);
    }

    /**
     * 计算增量分数
     *
     * @param articleStreamMessage
     * @return
     */
    private int computeScore(ArticleStreamMessage articleStreamMessage) {
        int score = 0;
        score += articleStreamMessage.getView() * 1 * 3;
        score += articleStreamMessage.getLike() * 3 * 3;
        score += articleStreamMessage.getComment() * 5 * 3;
        score += articleStreamMessage.getCollect() * 8 * 3;
        return score;
    }

    private int computeScore(ApArticle apArticle) {
        int score = 0;
        // 阅读1  点赞3  评论5 收藏8
        if (apArticle.getViews() != null) {
            score += apArticle.getViews() * 1;
        }
        if (apArticle.getLikes() != null) {
            score += apArticle.getLikes() * 3;
        }
        if (apArticle.getComment() != null) {
            score += apArticle.getComment() * 5;
        }
        if (apArticle.getCollection() != null) {
            score += apArticle.getCollection() * 8;
        }
        return score;
    }


}
