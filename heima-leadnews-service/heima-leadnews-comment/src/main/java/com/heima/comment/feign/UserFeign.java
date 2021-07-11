package com.heima.comment.feign;

import com.heima.comment.dto.ApUser;
import com.heima.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("leadnews-user")
public interface UserFeign {
    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    @GetMapping("/api/v1/user/{id}")
    public ResponseResult<ApUser> getUserById(@PathVariable("id") Integer id);
}
