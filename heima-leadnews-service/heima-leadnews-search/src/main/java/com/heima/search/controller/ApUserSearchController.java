package com.heima.search.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.dto.ResponseResult;
import com.heima.search.dto.UserSearchDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.search.service.IApUserSearchService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP用户搜索信息表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
@RestController
@RequestMapping("/api/v1/history")
@Api(tags = "APP用户搜索信息表接口")
@CrossOrigin
public class ApUserSearchController {

    @Autowired
    private IApUserSearchService apUserSearchService;


    @PostMapping("/load")
    public ResponseResult load(@RequestBody UserSearchDto dto) {
        return apUserSearchService.load(dto);
    }

    @PostMapping("/del")
    public ResponseResult del(@RequestBody UserSearchDto dto) {
        return apUserSearchService.del(dto);
    }

}
