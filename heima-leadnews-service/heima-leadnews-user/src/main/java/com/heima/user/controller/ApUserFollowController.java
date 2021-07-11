package com.heima.user.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.FollowBehaviorDto;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUserFollow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.user.service.IApUserFollowService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP用户关注信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-27
 */
@RestController
@RequestMapping("/api/v1/user_follow")
@Api(tags = "APP用户关注信息表接口")
@CrossOrigin
public class ApUserFollowController {

    @Autowired
    private IApUserFollowService apUserFollowService;

    @PostMapping("/getFollow")
    public ResponseResult<ApUserFollow> getFollow(@RequestBody FollowBehaviorDto dto) {
        return apUserFollowService.getFollow(dto);
    }

}
