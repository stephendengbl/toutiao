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
import com.heima.search.service.IApAssociateWordsService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 联想词表 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
@RestController
@RequestMapping("/api/v1/associate")
@Api(tags = "联想词表接口")
@CrossOrigin
public class ApAssociateWordsController {

    @Autowired
    private IApAssociateWordsService apAssociateWordsService;


    @PostMapping("/search")
    private ResponseResult load(@RequestBody UserSearchDto dto) {
        // return apAssociateWordsService.load(dto);
        return apAssociateWordsService.load2(dto);
    }

}
