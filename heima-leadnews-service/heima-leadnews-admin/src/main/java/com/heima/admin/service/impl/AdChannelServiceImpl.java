package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.dto.ChannelDto;
import com.heima.admin.entity.AdChannel;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.service.IAdChannelService;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdChannelServiceImpl extends ServiceImpl<AdChannelMapper, AdChannel> implements IAdChannelService {
    @Override
    public ResponseResult listByName(ChannelDto dto) {
        // 根据名称模糊查询分页列表

        // 参数校验
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 构建分页条件 参数 1 当前页 2 每页条数
        IPage<AdChannel> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<AdChannel> query = new LambdaQueryWrapper<>();
        // 判断name是否为空
        if (!StringUtils.isEmpty(dto.getName())) {
            // 参数 1 字段 2 值
            query.like(AdChannel::getName, dto.getName());
        }
        IPage<AdChannel> iPage = this.page(page, query);
        // 返回通用的分页响应对象
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), iPage.getTotal(), iPage.getRecords());
        return result;
    }

    @Override
    public ResponseResult saveChannel(AdChannel entity) {
        // 需求分析
        // 1. 判断参数是否有效
        if (entity == null || StringUtils.isEmpty(entity.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2. 判断频道名称是否已经存在

        // select count(1) from ad_channel where name = #name
        LambdaQueryWrapper<AdChannel> query = new LambdaQueryWrapper<>();
        query.eq(AdChannel::getName, entity.getName());
        int count = this.count(query);
        if (count <= 0) {
            // 3. 不存在保存频道
            this.save(entity);
        }
        // 返回新增的频道ID
        return ResponseResult.okResult(entity.getId());
    }

    @Override
    public ResponseResult deleteChannel(Integer id) {
        // 需求
        // 1. 根据id查询频道
        AdChannel adChannel = this.getById(id);
        if (adChannel == null) {
            // 频道不存在,提示
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 2. 如果频道存在,需要判断状态是否有效
        if (adChannel.getStatus()) {
            // 3. 如果是有效,提示不能删除
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_CAN_NOT_DELETE);
        }
        // 4. 无效,可以删除
        this.removeById(id);
        return ResponseResult.okResult();
    }
}
