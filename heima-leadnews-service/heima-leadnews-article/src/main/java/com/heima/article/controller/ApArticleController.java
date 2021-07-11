package com.heima.article.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.dto.ArticleDto;
import com.heima.article.dto.ArticleHomeDto;
import com.heima.article.dto.ArticleInfoDto;
import com.heima.article.dto.CollectionBehaviorDto;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.article.service.IApArticleService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 文章信息表，存储已发布的文章 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-25
 */
@RestController
@RequestMapping("/api/v1/article")
@Api(tags = "文章信息表，存储已发布的文章接口")
@CrossOrigin
public class ApArticleController {

    @Autowired
    private IApArticleService apArticleService;

    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult<Long> saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }


    /**
     * 加载文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult loadArticle(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticle(dto, 0);
    }

    /**
     * 上拉获取更多文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticle(dto, 1);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticle(dto, 0);
    }

    @PostMapping("/load_article_info")
    public ResponseResult loadArticleInfo(@RequestBody ArticleInfoDto dto) {
        return apArticleService.loadArticleInfo(dto);
    }


    @PostMapping("/collection")
    public ResponseResult collect(@RequestBody CollectionBehaviorDto dto) {
        return apArticleService.collect(dto);
    }

    @PostMapping("/load_article_behavior")
    public ResponseResult loadBehavior(@RequestBody ArticleInfoDto dto) {
        return apArticleService.loadBehavior(dto);
    }

}
