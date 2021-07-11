package com.heima.user.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.user.service.IApUserService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP用户信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "APP用户信息表接口")
@CrossOrigin
public class ApUserController {

    @Autowired
    private IApUserService apUserService;

    /**
     * 关注和取消关注
     *
     * @param dto
     * @return
     */
    @PostMapping("/user_follow")
    public ResponseResult follow(@RequestBody UserRelationDto dto) {
        return apUserService.follow(dto);
    }


    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<ApUser> getUserById(@PathVariable("id") Integer id) {
        return ResponseResult.okResult(apUserService.getById(id));
    }
}
