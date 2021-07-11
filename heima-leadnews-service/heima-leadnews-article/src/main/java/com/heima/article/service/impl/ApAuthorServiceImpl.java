package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.entity.ApAuthor;
import com.heima.article.mapper.ApAuthorMapper;
import com.heima.article.service.IApAuthorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP文章作者信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@Service
public class ApAuthorServiceImpl extends ServiceImpl<ApAuthorMapper, ApAuthor> implements IApAuthorService {

    @Override
    public ResponseResult<ApAuthor> saveAuthor(ApAuthor entity) {
        // 需求 保存作者信息
        if (entity == null || entity.getUserId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 判断该作者是否已经存在
        // 根据user_id查询记录
        LambdaQueryWrapper<ApAuthor> query = new LambdaQueryWrapper<>();
        query.eq(ApAuthor::getUserId, entity.getUserId());
        ApAuthor apAuthor = this.getOne(query);
        if (apAuthor != null) {
            return ResponseResult.okResult(apAuthor);
        }
        // 不存在保存作者
        this.save(entity);
        return ResponseResult.okResult(entity);
    }
}
