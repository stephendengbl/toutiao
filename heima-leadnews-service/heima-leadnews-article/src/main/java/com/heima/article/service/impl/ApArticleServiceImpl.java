package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.article.dto.*;
import com.heima.article.entity.ApArticle;
import com.heima.article.entity.ApArticleContent;
import com.heima.article.entity.ApAuthor;
import com.heima.article.entity.ApCollection;
import com.heima.article.feign.BehaviorFeign;
import com.heima.article.feign.UserFeign;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.IApArticleContentService;
import com.heima.article.service.IApArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.service.IApAuthorService;
import com.heima.article.service.IApCollectionService;
import com.heima.article.vo.HotArticleVo;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-25
 */
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements IApArticleService {

    // 注入文章内容服务接口
    @Autowired
    private IApArticleContentService contentService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult<Long> saveArticle(ArticleDto dto) {
        // 需求分析
        // 1. 判断是否有id
        if (dto.getId() == null) {
            // 2. 没有id,新增文章
            ApArticle apArticle = new ApArticle();
            BeanUtils.copyProperties(dto, apArticle);
            this.save(apArticle);
            // 2.1 保存文章内容
            ApArticleContent content = new ApArticleContent();
            content.setArticleId(apArticle.getId());
            content.setContent(dto.getContent());
            contentService.save(content);
            return ResponseResult.okResult(apArticle.getId());
        }


        // 3. 有id,修改文章
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);
        this.updateById(apArticle);
        // 3.1 修改文章内容
        LambdaUpdateWrapper<ApArticleContent> update = new LambdaUpdateWrapper<>();
        // 设置查询条件
        update.eq(ApArticleContent::getArticleId, dto.getId());
        // 设置更新条件
        update.set(ApArticleContent::getContent, dto.getContent());
        contentService.update(update);
        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 加载文章列表
     *
     * @param dto
     * @param type 0 是加载最新文章  1 加载更多文章
     * @return
     */
    @Override
    public ResponseResult loadArticle(ArticleHomeDto dto, int type) {

        // 需求分析
        // 1. 默认查询10篇文章
        // 2. 根据频道ID查询
        // 3. 根据发布时间倒序排列
        // 4. 如果是需要加载更多文章,前端页面获取当前页面发布时间最小的值,查询比这个时间更小的数据
        // 5. 如果是要加载最新文章,前端页面获取当前页面发布时间最大的值,查询比这个时间更大的数据
        // 6. 过滤已下架或者已删除的文章
        if (dto.getSize() == null || dto.getSize() <= 0 || dto.getSize() >= 50) {
            dto.setSize(10);
        }
        IPage<ApArticle> page = new Page<>(1, dto.getSize());
        LambdaQueryWrapper<ApArticle> query = new LambdaQueryWrapper<>();
        if (dto.getChannelId() != null && dto.getChannelId() != 0) {
            query.eq(ApArticle::getChannelId, dto.getChannelId());
        }
        query.orderByDesc(ApArticle::getPublishTime);
        query.eq(ApArticle::getIsDelete, false);
        query.eq(ApArticle::getIsDown, false);
        if (type == 1) {
            // 如果是需要加载更多文章,前端页面获取当前页面发布时间最小的值,查询比这个时间更小的数据
            query.lt(ApArticle::getPublishTime, dto.getMinTime());
        }
        if (type == 0) {
            // 如果是要加载最新文章,前端页面获取当前页面发布时间最大的值,查询比这个时间更大的数据
            query.gt(ApArticle::getPublishTime, dto.getMaxTime());
        }
        IPage<ApArticle> iPage = this.page(page, query);
        return ResponseResult.okResult(iPage.getRecords());
    }

    @Override
    public ResponseResult load2(ArticleHomeDto dto, Integer type, boolean firstPage) {
        // 判断是否是首页,是首页的话从redis中查询数据
        if (firstPage) {
            int channelId = 0;
            if (dto.getChannelId() != null) {
                channelId = dto.getChannelId();
            }
            String redisKey = "hot_article_first_page_" + channelId;
            String json = redisTemplate.opsForValue().get(redisKey);
            List<HotArticleVo> hotArticleVos = JSON.parseArray(json, HotArticleVo.class);
            return ResponseResult.okResult(hotArticleVos);
        }
        // 否则走数据库查询
        return loadArticle(dto, type);
    }


    @Override
    public ResponseResult loadArticleInfo(ArticleInfoDto dto) {
        // 1. 根据文章id查询文章
        ApArticle apArticle = this.getById(dto.getArticleId());
        if (apArticle == null || apArticle.getIsDown() || apArticle.getIsDelete()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 2. 查询文章内容
        LambdaQueryWrapper<ApArticleContent> query = new LambdaQueryWrapper<>();
        query.eq(ApArticleContent::getArticleId, dto.getArticleId());
        ApArticleContent apArticleContent = contentService.getOne(query);
        // 3. 构建返回对象
        Map<String, Object> map = new HashMap<>();
        map.put("config", apArticle);
        map.put("content", apArticleContent);
        return ResponseResult.okResult(map);
    }


    @Autowired
    private BehaviorFeign behaviorFeign;

    @Autowired
    private IApCollectionService collectionService;

    @Override
    public ResponseResult collect(CollectionBehaviorDto dto) {
        // 保存ApCollection对象
        // 获取当前登录的用户
        User user = AppThreadLocalUtil.get();
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        // 远程调用行为微服务获取entryId
        ResponseResult<ApBehaviorEntry> entryResponseResult = behaviorFeign.getEntry(entryDto);
        if (entryResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApBehaviorEntry behaviorEntry = entryResponseResult.getData();
            if (behaviorEntry != null) {
                ApCollection collection = new ApCollection();
                collection.setEntryId(behaviorEntry.getId());
                collection.setArticleId(dto.getArticleId());
                collection.setCollectionTime(new Date());
                if (dto.getOperation() == 0) {
                    // 查询是否有记录
                    LambdaQueryWrapper<ApCollection> query = new LambdaQueryWrapper<>();
                    query.eq(ApCollection::getEntryId, behaviorEntry.getId());
                    query.eq(ApCollection::getArticleId, dto.getArticleId());
                    ApCollection apCollection = collectionService.getOne(query);
                    // 保存
                    if (apCollection == null) {
                        collectionService.save(collection);
                    }
                } else {
                    // 删除记录
                    LambdaQueryWrapper<ApCollection> query = new LambdaQueryWrapper<>();
                    query.eq(ApCollection::getEntryId, behaviorEntry.getId());
                    query.eq(ApCollection::getArticleId, dto.getArticleId());
                    collectionService.remove(query);
                }
            }
        }
        // 判断操作是收藏还是取消收藏  收藏 保存  取消 删除记录
        return ResponseResult.okResult();
    }

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private IApAuthorService authorService;

    @Override
    public ResponseResult loadBehavior(ArticleInfoDto dto) {

        boolean isfollow = false;
        boolean islike = false;
        boolean isunlike = false;
        boolean iscollection = false;

        User user = AppThreadLocalUtil.get();

        // 查询用户的关注记录
        if (user != null) {
            FollowBehaviorDto followBehaviorDto = new FollowBehaviorDto();
            followBehaviorDto.setUserId(user.getUserId());
            ApAuthor apAuthor = authorService.getById(dto.getAuthorId());
            followBehaviorDto.setFollowId(apAuthor.getUserId());
            ResponseResult<ApUserFollow> userFollowResponseResult = userFeign.getFollow(followBehaviorDto);
            if (userFollowResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
                ApUserFollow apUserFollow = userFollowResponseResult.getData();
                if (apUserFollow != null) {
                    isfollow = true;
                }
            }
        }


        // 查询用户的点赞记录
        LikesBehaviorDto likesBehaviorDto = new LikesBehaviorDto();
        likesBehaviorDto.setArticleId(dto.getArticleId());
        if (user != null) {
            likesBehaviorDto.setUserId(user.getUserId());
        }
        likesBehaviorDto.setEquipmentId(dto.getEquipmentId());
        ResponseResult<ApLikesBehavior> likesBehaviorResponseResult = behaviorFeign.getLikesBehavior(likesBehaviorDto);
        if (likesBehaviorResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApLikesBehavior apLikesBehavior = likesBehaviorResponseResult.getData();
            if (apLikesBehavior != null && apLikesBehavior.getOperation() == 0) {
                islike = true;
            }
        }
        // 查询用户的不喜欢记录
        UnLikesBehaviorDto unLikesBehaviorDto = new UnLikesBehaviorDto();
        unLikesBehaviorDto.setArticleId(dto.getArticleId());
        unLikesBehaviorDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            unLikesBehaviorDto.setUserId(user.getUserId());
        }
        ResponseResult<ApUnlikesBehavior> unlikesBehaviorResponseResult = behaviorFeign.getUnlikesBehavior(unLikesBehaviorDto);
        if (unlikesBehaviorResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApUnlikesBehavior apUnlikesBehavior = unlikesBehaviorResponseResult.getData();
            if (apUnlikesBehavior != null && apUnlikesBehavior.getType() == 0) {
                isunlike = true;
            }
        }
        // 查询用户的收藏记录
        LambdaQueryWrapper<ApCollection> queryWrapper = new LambdaQueryWrapper<>();
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ResponseResult<ApBehaviorEntry> entryResponseResult = behaviorFeign.getEntry(entryDto);
        if (entryResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApBehaviorEntry behaviorEntry = entryResponseResult.getData();
            queryWrapper.eq(ApCollection::getEntryId, behaviorEntry.getId());
            queryWrapper.eq(ApCollection::getArticleId, dto.getArticleId());
            ApCollection apCollection = collectionService.getOne(queryWrapper);
            if (apCollection != null) {
                iscollection = true;
            }
        }

        Map<String, Boolean> map = new HashMap<>();
        map.put("isfollow", isfollow);
        map.put("islike", islike);
        map.put("isunlike", isunlike);
        map.put("iscollection", iscollection);

        return ResponseResult.okResult(map);
    }
}
