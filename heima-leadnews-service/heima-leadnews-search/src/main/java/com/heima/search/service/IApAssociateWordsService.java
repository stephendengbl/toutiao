package com.heima.search.service;

import com.heima.common.dto.ResponseResult;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.entity.ApAssociateWords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 联想词表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
public interface IApAssociateWordsService extends IService<ApAssociateWords> {

    ResponseResult load(UserSearchDto dto);

    ResponseResult load2(UserSearchDto dto);
}
