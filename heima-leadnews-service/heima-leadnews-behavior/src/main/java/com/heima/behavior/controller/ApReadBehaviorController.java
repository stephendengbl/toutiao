package com.heima.behavior.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.behavior.dto.ReadBehaviorDto;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.behavior.service.IApReadBehaviorService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP阅读行为表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@RestController
@RequestMapping("/api/v1/read_behavior")
@Api(tags = "APP阅读行为表接口")
@CrossOrigin
public class ApReadBehaviorController {

    @Autowired
    private IApReadBehaviorService apReadBehaviorService;


    @PostMapping
    public ResponseResult saveReadBehavior(@RequestBody ReadBehaviorDto dto) {
        return apReadBehaviorService.saveReadBehavior(dto);
    }
}
