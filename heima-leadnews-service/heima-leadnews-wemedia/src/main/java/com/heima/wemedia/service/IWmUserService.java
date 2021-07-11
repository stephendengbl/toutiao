package com.heima.wemedia.service;

import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.WmLoginDto;
import com.heima.wemedia.entity.WmUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 自媒体用户信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
public interface IWmUserService extends IService<WmUser> {

    ResponseResult<WmUser> saveWmUser(WmUser entity);

    ResponseResult login(WmLoginDto dto);
}
