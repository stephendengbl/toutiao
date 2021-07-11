package com.heima.behavior.service;

import com.heima.behavior.dto.LikesBehaviorDto;
import com.heima.behavior.entity.ApLikesBehavior;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * APP点赞行为表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
public interface IApLikesBehaviorService extends IService<ApLikesBehavior> {

    ResponseResult saveLikesBehavior(LikesBehaviorDto dto);

    ResponseResult getLikesBehavior(LikesBehaviorDto dto);
}
