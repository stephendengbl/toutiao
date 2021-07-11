package com.heima.user.feign;

import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.ApAuthor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 文章远程接口
 */
@FeignClient("leadnews-article")
public interface ArticleFeign {

    /**
     * 保存文章作者
     * @param entity
     * @return
     */
    @PostMapping("/api/v1/author")
    public ResponseResult<ApAuthor> saveAuthor(@RequestBody ApAuthor entity);

    /**
     * 根据id查询作者
     * @param id
     * @return
     */
    @GetMapping("/api/v1/author/{id}")
    public ResponseResult<ApAuthor> getAuthorById(@PathVariable("id") Integer id);
}
