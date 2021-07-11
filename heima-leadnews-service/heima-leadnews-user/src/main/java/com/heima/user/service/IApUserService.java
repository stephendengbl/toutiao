package com.heima.user.service;

import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.LoginDto;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP用户信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
public interface IApUserService extends IService<ApUser> {

    ResponseResult login(LoginDto dto);

    ResponseResult follow(UserRelationDto dto);
}
