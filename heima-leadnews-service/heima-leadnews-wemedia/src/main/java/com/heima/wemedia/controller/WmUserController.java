package com.heima.wemedia.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.entity.WmUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.wemedia.service.IWmUserService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 自媒体用户信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "自媒体用户信息表接口")
@CrossOrigin
public class WmUserController {

    @Autowired
    private IWmUserService wmUserService;

    /**
     * 保存自媒体用户
     *
     * @param entity
     * @return
     */
    @PostMapping
    public ResponseResult<WmUser> saveWmUser(@RequestBody WmUser entity) {
        return wmUserService.saveWmUser(entity);
    }

    /**
     * 更新自媒体用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    public ResponseResult updateWmUser(@RequestBody WmUser entity) {
        wmUserService.updateById(entity);
        return ResponseResult.okResult();
    }

    /**
     * 根据id查询自媒体用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<WmUser> getUserById(@PathVariable("id") Integer id) {
        WmUser wmUser = wmUserService.getById(id);
        return ResponseResult.okResult(wmUser);
    }

}
