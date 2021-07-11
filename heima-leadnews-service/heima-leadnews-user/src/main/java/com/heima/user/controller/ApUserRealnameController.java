package com.heima.user.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.AuthDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.user.service.IApUserRealnameService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP实名认证信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "APP实名认证信息表接口")
@CrossOrigin
public class ApUserRealnameController {

    @Autowired
    private IApUserRealnameService apUserRealnameService;


    @PostMapping("/list")
    public ResponseResult listByStatus(@RequestBody AuthDto dto) {
        return apUserRealnameService.listByStatus(dto);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return apUserRealnameService.auth(dto, 1);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return apUserRealnameService.auth(dto, 0);
    }

}
