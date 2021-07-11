package com.heima.search.service;

import com.heima.common.dto.ResponseResult;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.entity.ApUserSearch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP用户搜索信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
public interface IApUserSearchService extends IService<ApUserSearch> {

    ResponseResult load(UserSearchDto dto);

    ResponseResult del(UserSearchDto dto);
}
