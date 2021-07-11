package com.heima.behavior.service;

import com.heima.behavior.dto.UnLikesBehaviorDto;
import com.heima.behavior.entity.ApUnlikesBehavior;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * APP不喜欢行为表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
public interface IApUnlikesBehaviorService extends IService<ApUnlikesBehavior> {

    ResponseResult saveUnlikesBehavior(UnLikesBehaviorDto dto);

    ResponseResult<ApUnlikesBehavior> getUnlikesBehavior(UnLikesBehaviorDto dto);
}
