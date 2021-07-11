package com.heima.admin.controller;

import com.heima.admin.dto.ChannelDto;
import com.heima.admin.entity.AdChannel;
import com.heima.admin.service.IAdChannelService;
import com.heima.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
@Api(tags = "频道管理API")
public class AdChannelController {

    // 注入服务接口
    @Autowired
    private IAdChannelService channelService;

    /**
     * 根据名称模糊查询分页列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "根据名称模糊查询分页列表", notes = "author:mcm")  // value指名称  notes 备注
    @ApiImplicitParam(name = "dto", value = "查询对象", required = true, dataType = "ChannelDto")
    public ResponseResult listByName(@RequestBody ChannelDto dto) {
        return channelService.listByName(dto);
    }

    @GetMapping("/channels")
    public ResponseResult list() {
        List<AdChannel> list = channelService.list();
        return ResponseResult.okResult(list);
    }

    /**
     * 保存频道
     *
     * @param entity
     * @return
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存频道", notes = "author:mcm")  // value指名称  notes 备注
    @ApiImplicitParam(name = "entity", value = "频道对象", required = true, dataType = "AdChannel")
    public ResponseResult saveChannel(@RequestBody AdChannel entity) {
        entity.setCreatedTime(new Date());
        return channelService.saveChannel(entity);
    }

    /**
     * 更新频道
     *
     * @param entity
     * @return
     */
    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody AdChannel entity) {
        // 业务逻辑简单 可以直接在controller层面处理
        channelService.updateById(entity);
        return ResponseResult.okResult();
    }

    /**
     * 根据id删除频道
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteChannel(@PathVariable("id") Integer id) {
        return channelService.deleteChannel(id);
    }
}
