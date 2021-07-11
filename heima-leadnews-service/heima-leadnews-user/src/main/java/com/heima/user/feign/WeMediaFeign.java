package com.heima.user.feign;

import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 自媒体远程接口
 */
@FeignClient(value = "leadnews-wemedia")
public interface WeMediaFeign {

    /**
     * 保存自媒体用户
     *
     * @param entity
     * @return
     */
    @PostMapping("/api/v1/user")
    public ResponseResult<WmUser> saveWmUser(@RequestBody WmUser entity);

    /**
     * 更新自媒体用户
     *
     * @param entity
     * @return
     */
    @PutMapping("/api/v1/user")
    public ResponseResult updateWmUser(@RequestBody WmUser entity);
}
