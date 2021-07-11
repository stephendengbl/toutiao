package com.heima.wemedia.service;

import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.NewsAuthDto;
import com.heima.wemedia.dto.WmNewsDto;
import com.heima.wemedia.dto.WmNewsPageDto;
import com.heima.wemedia.entity.WmNews;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.wemedia.vo.WmNewsVo;

import java.util.List;

/**
 * <p>
 * 自媒体图文内容信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-22
 */
public interface IWmNewsService extends IService<WmNews> {

    ResponseResult listByCondition(WmNewsPageDto dto);

    ResponseResult submit(WmNewsDto dto);

    ResponseResult deleteById(Integer id);

    ResponseResult downOrUp(WmNewsDto dto);

    ResponseResult<List<Integer>> getRelease();

    PageResponseResult findPageByName(NewsAuthDto dto);

    ResponseResult<WmNewsVo> findNewsVoById(Integer id);
}
