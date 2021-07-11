package com.heima.behavior.service;

import com.heima.behavior.dto.FollowBehaviorDto;
import com.heima.behavior.entity.ApFollowBehavior;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP关注行为表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
public interface IApFollowBehaviorService extends IService<ApFollowBehavior> {

    void saveBehavior(FollowBehaviorDto followBehaviorDto);
}
