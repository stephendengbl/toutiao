package com.heima.user.service;

import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.FollowBehaviorDto;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUserFollow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP用户关注信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-27
 */
public interface IApUserFollowService extends IService<ApUserFollow> {

    ResponseResult getFollow(FollowBehaviorDto dto);
}
