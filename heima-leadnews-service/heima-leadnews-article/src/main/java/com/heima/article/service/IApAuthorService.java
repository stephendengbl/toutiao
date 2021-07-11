package com.heima.article.service;

import com.heima.article.entity.ApAuthor;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dto.ResponseResult;

/**
 * <p>
 * APP文章作者信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
public interface IApAuthorService extends IService<ApAuthor> {

    ResponseResult<ApAuthor> saveAuthor(ApAuthor entity);
}
