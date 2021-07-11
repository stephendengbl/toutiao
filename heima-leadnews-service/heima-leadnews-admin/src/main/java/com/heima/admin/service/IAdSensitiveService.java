package com.heima.admin.service;

import com.heima.admin.dto.SensitiveDto;
import com.heima.admin.entity.AdSensitive;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * 敏感词信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-18
 */
public interface IAdSensitiveService extends IService<AdSensitive> {

    ResponseResult listByName(SensitiveDto dto);

    ResponseResult saveSensitive(AdSensitive entity);

    ResponseResult updateSensitive(AdSensitive entity);
}
