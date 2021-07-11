package com.heima.behavior.service;

import com.heima.behavior.dto.ReadBehaviorDto;
import com.heima.behavior.entity.ApReadBehavior;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * APP阅读行为表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
public interface IApReadBehaviorService extends IService<ApReadBehavior> {

    ResponseResult saveReadBehavior(ReadBehaviorDto dto);
}
