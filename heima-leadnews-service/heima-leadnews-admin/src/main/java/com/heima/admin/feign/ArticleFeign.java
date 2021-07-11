package com.heima.admin.feign;

import com.heima.admin.dto.ArticleDto;
import com.heima.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-article")
public interface ArticleFeign {
    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article")
    public ResponseResult<Long> saveArticle(@RequestBody ArticleDto dto);
}
