package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.admin.dto.ChannelDto;
import com.heima.admin.entity.AdChannel;
import com.heima.common.dto.ResponseResult;

public interface IAdChannelService extends IService<AdChannel> {
    ResponseResult listByName(ChannelDto dto);

    ResponseResult saveChannel(AdChannel entity);

    ResponseResult deleteChannel(Integer id);
}
