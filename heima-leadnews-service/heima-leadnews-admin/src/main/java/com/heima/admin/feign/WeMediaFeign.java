package com.heima.admin.feign;

import com.heima.admin.dto.NewsAuthDto;
import com.heima.admin.dto.WmNews;
import com.heima.admin.dto.WmUser;
import com.heima.admin.vo.WmNewsVo;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("leadnews-wemedia")
public interface WeMediaFeign {

    /**
     * 根据id查询自媒体文章
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/{id}")
    public ResponseResult<WmNews> getById(@PathVariable("id") Integer id);

    /**
     * 修改自媒体文章
     *
     * @param entity
     * @return
     */
    @PutMapping("/api/v1/news")
    public ResponseResult updateWmNews(@RequestBody WmNews entity);

    /**
     * 根据id查询自媒体用户
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/user/{id}")
    public ResponseResult<WmUser> getUserById(@PathVariable("id") Integer id);

    /**
     * 获取待发布的文章id
     * @return
     */
    @GetMapping("/api/v1/news/getRelease")
    public ResponseResult<List<Integer>> getRelease();

    @PostMapping("/api/v1/news/findPageByName")
    public PageResponseResult findPageByName(@RequestBody NewsAuthDto dto);

    @GetMapping("/api/v1/news/findNewsVoById/{id}")
    public ResponseResult<WmNewsVo> findNewsVoById(@PathVariable("id") Integer id);
}
