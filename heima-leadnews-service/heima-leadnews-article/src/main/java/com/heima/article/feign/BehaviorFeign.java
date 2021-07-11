package com.heima.article.feign;

import com.heima.article.dto.*;
import com.heima.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-behavior")
public interface BehaviorFeign {
    @PostMapping("/api/v1/behavior_entry/getEntry")
    public ResponseResult<ApBehaviorEntry> getEntry(@RequestBody EntryDto dto);


    @PostMapping("/api/v1/likes_behavior/getLike")
    public ResponseResult<ApLikesBehavior> getLikesBehavior(@RequestBody LikesBehaviorDto dto);

    @PostMapping("/api/v1/unlikes_behavior/getUnLikes")
    public ResponseResult<ApUnlikesBehavior> getUnlikesBehavior(@RequestBody UnLikesBehaviorDto dto);
}
