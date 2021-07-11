package com.heima.article.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.entity.ApAuthor;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.article.service.IApAuthorService;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP文章作者信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/api/v1/author")
@Api(tags = "APP文章作者信息表接口")
@CrossOrigin
public class ApAuthorController{

    @Autowired
    private IApAuthorService apAuthorService;

    /**
     * 保存文章作者
     * @param entity
     * @return
     */
    @PostMapping
    public ResponseResult<ApAuthor> saveAuthor(@RequestBody ApAuthor entity){
        return apAuthorService.saveAuthor(entity);
    }

    /**
     * 根据id查询作者
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<ApAuthor> getAuthorById(@PathVariable("id") Integer id){
        ApAuthor apAuthor = apAuthorService.getById(id);
        return ResponseResult.okResult(apAuthor);
    }

}
