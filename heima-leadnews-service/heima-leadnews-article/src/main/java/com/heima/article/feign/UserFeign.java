package com.heima.article.feign;

import com.heima.article.dto.ApUserFollow;
import com.heima.article.dto.FollowBehaviorDto;
import com.heima.article.dto.UserRelationDto;
import com.heima.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-user")
public interface UserFeign {
    @PostMapping("/api/v1/user_follow/getFollow")
    public ResponseResult<ApUserFollow> getFollow(@RequestBody FollowBehaviorDto dto);
}
