package com.heima.behavior.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.behavior.service.IApBehaviorEntryService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP行为实体表,一个行为实体可能是用户或者设备，或者其它 前端控制器
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@RestController
@RequestMapping("/api/v1/behavior_entry")
@Api(tags = "APP行为实体表,一个行为实体可能是用户或者设备，或者其它接口")
@CrossOrigin
public class ApBehaviorEntryController {

    @Autowired
    private IApBehaviorEntryService apBehaviorEntryService;


    @PostMapping("/getEntry")
    public ResponseResult<ApBehaviorEntry> getEntry(@RequestBody EntryDto dto) {
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryService.getEntry(dto);
        return ResponseResult.okResult(apBehaviorEntry);
    }


}
