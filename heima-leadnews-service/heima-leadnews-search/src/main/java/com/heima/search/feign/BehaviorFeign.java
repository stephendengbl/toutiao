package com.heima.search.feign;

import com.heima.common.dto.ResponseResult;
import com.heima.search.dto.ApBehaviorEntry;
import com.heima.search.dto.EntryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-behavior")
public interface BehaviorFeign {
    @PostMapping("/api/v1/behavior_entry/getEntry")
    public ResponseResult<ApBehaviorEntry> getEntry(@RequestBody EntryDto dto);
}
