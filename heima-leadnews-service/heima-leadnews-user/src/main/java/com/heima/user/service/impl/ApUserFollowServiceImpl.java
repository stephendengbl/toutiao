package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppThreadLocalUtil;
import com.heima.user.dto.ApAuthor;
import com.heima.user.dto.FollowBehaviorDto;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUserFollow;
import com.heima.user.feign.ArticleFeign;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.service.IApUserFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP用户关注信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-27
 */
@Service
public class ApUserFollowServiceImpl extends ServiceImpl<ApUserFollowMapper, ApUserFollow> implements IApUserFollowService {


    @Autowired
    private ArticleFeign articleFeign;

    @Override
    public ResponseResult getFollow(FollowBehaviorDto dto) {

        // 根据用户id和关注的id查询
        LambdaQueryWrapper<ApUserFollow> query = new LambdaQueryWrapper<>();
        query.eq(ApUserFollow::getUserId,dto.getUserId());
        query.eq(ApUserFollow::getFollowId,dto.getFollowId());
        ApUserFollow apUserFollow = this.getOne(query);
        return ResponseResult.okResult(apUserFollow);
    }
}
