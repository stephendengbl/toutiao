package com.heima.wemedia.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.NewsAuthDto;
import com.heima.wemedia.dto.WmNewsDto;
import com.heima.wemedia.dto.WmNewsPageDto;
import com.heima.wemedia.entity.WmNews;
import com.heima.wemedia.vo.WmNewsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.wemedia.service.IWmNewsService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 自媒体图文内容信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-22
 */
@RestController
@RequestMapping("/api/v1/news")
@Api(tags = "自媒体图文内容信息表接口")
@CrossOrigin
public class WmNewsController {

    @Autowired
    private IWmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult listByCondition(@RequestBody WmNewsPageDto dto) {
        return wmNewsService.listByCondition(dto);
    }


    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto dto) {
        return wmNewsService.submit(dto);
    }

    /**
     * 根据id查询自媒体文章
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable("id") Integer id) {
        WmNews wmNews = wmNewsService.getById(id);
        return ResponseResult.okResult(wmNews);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return wmNewsService.deleteById(id);
    }

    @PutMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 修改自媒体文章
     *
     * @param entity
     * @return
     */
    @PutMapping
    public ResponseResult updateWmNews(@RequestBody WmNews entity) {
        wmNewsService.updateById(entity);
        return ResponseResult.okResult();
    }

    /**
     * 获取待发布的文章id
     *
     * @return
     */
    @GetMapping("/getRelease")
    public ResponseResult<List<Integer>> getRelease() {
        return wmNewsService.getRelease();
    }

    @PostMapping("/findPageByName")
    public PageResponseResult findPageByName(@RequestBody NewsAuthDto dto) {
        return wmNewsService.findPageByName(dto);
    }

    @GetMapping("/findNewsVoById/{id}")
    public ResponseResult<WmNewsVo> findNewsVoById(@PathVariable("id") Integer id) {
        return wmNewsService.findNewsVoById(id);
    }

}
