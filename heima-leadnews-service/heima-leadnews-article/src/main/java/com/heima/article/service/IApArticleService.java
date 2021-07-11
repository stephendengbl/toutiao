package com.heima.article.service;

import com.heima.article.dto.ArticleDto;
import com.heima.article.dto.ArticleHomeDto;
import com.heima.article.dto.ArticleInfoDto;
import com.heima.article.dto.CollectionBehaviorDto;
import com.heima.article.entity.ApArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-25
 */
public interface IApArticleService extends IService<ApArticle> {

    ResponseResult<Long> saveArticle(ArticleDto dto);

    ResponseResult loadArticle(ArticleHomeDto dto, int type);

    ResponseResult load2(ArticleHomeDto dto,Integer type, boolean firstPage);

    ResponseResult loadArticleInfo(ArticleInfoDto dto);

    ResponseResult collect(CollectionBehaviorDto dto);

    ResponseResult loadBehavior(ArticleInfoDto dto);
}
