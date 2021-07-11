package com.heima.admin.service;

import com.heima.admin.dto.NewsAuthDto;
import com.heima.common.dto.ResponseResult;

public interface IAuthService {
    ResponseResult list(NewsAuthDto dto);

    ResponseResult one(Integer id);

    ResponseResult auth(NewsAuthDto dto);
}
