package com.heima.wemedia.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.WmMaterialDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.wemedia.service.IWmMaterialService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 自媒体图文素材信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-20
 */
@RestController
@RequestMapping("/api/v1/material")
@Api(tags = "自媒体图文素材信息表接口")
@CrossOrigin
public class WmMaterialController {

    @Autowired
    private IWmMaterialService wmMaterialService;

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult upload(MultipartFile file) {
        return wmMaterialService.upload(file);
    }

    /**
     * 加载图片列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult listByCollection(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.listByCollection(dto);
    }

    /**
     * 删除图片
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return wmMaterialService.deleteById(id);
    }

    /**
     * 收藏图片
     *
     * @param id
     * @return
     */
    @PutMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable("id") Integer id) {
        return wmMaterialService.updateStatus(id, 1);
    }

    /**
     * 取消收藏
     *
     * @param id
     * @return
     */
    @PutMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable("id") Integer id) {
        return wmMaterialService.updateStatus(id, 0);
    }
}
