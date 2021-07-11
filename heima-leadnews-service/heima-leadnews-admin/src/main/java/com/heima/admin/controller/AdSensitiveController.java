package com.heima.admin.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.admin.dto.SensitiveDto;
import com.heima.admin.entity.AdSensitive;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.admin.service.IAdSensitiveService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 敏感词信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-18
 */
@RestController
@RequestMapping("/api/v1/sensitive")
@Api(tags = "敏感词信息表接口")
@CrossOrigin // 允许跨域访问
public class AdSensitiveController {

    @Autowired
    private IAdSensitiveService adSensitiveService;

    /**
     * 根据敏感词模糊查询分页结果
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult listByName(@RequestBody SensitiveDto dto) {
        return adSensitiveService.listByName(dto);
    }

    /**
     * 保存敏感词
     * @param entity
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody AdSensitive entity) {
        return adSensitiveService.saveSensitive(entity);
    }

    /**
     * 更新敏感词
     * @param entity
     * @return
     */
    @PutMapping("/update")
    public ResponseResult updateSensitive(@RequestBody AdSensitive entity) {
        return adSensitiveService.updateSensitive(entity);
    }

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteSensitive(@PathVariable("id") Integer id) {
        adSensitiveService.removeById(id);
        return ResponseResult.okResult();
    }

}
