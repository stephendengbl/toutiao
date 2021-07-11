package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppJwtUtil;
import com.heima.common.util.AppThreadLocalUtil;
import com.heima.user.dto.ApAuthor;
import com.heima.user.dto.FollowBehaviorDto;
import com.heima.user.dto.LoginDto;
import com.heima.user.dto.UserRelationDto;
import com.heima.user.entity.ApUser;
import com.heima.user.entity.ApUserFollow;
import com.heima.user.feign.ArticleFeign;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.IApUserFollowService;
import com.heima.user.service.IApUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * APP用户信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements IApUserService {

    @Override
    public ResponseResult login(LoginDto dto) {

        // 需求  判断登录方式
        if (dto == null || (StringUtils.isEmpty(dto.getPhone()) && StringUtils.isEmpty(dto.getEquipmentId()))) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 1. 使用设备id进行登录,生成token时使用0
        if (!StringUtils.isEmpty(dto.getEquipmentId()) && StringUtils.isEmpty(dto.getPhone())) {
            String token = AppJwtUtil.getToken(0L);
            // 前端要求的返回信息
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", token);
            return ResponseResult.okResult(map);
        }

        // 2. 手机号和密码不为空,使用手机号和密码进行登录
        if (StringUtils.isEmpty(dto.getPhone()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        LambdaQueryWrapper<ApUser> query = new LambdaQueryWrapper<>();
        query.eq(ApUser::getPhone, dto.getPhone());
        // 1.1 根据手机号查询用户
        ApUser apUser = this.getOne(query);
        if (apUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 1.2 使用用户传入的密码+查询到用户的盐拼接后进行MD5加密
        String pwd = dto.getPassword() + apUser.getSalt();
        String pass = DigestUtils.md5DigestAsHex(pwd.getBytes());
        if (pass.equals(apUser.getPassword())) {
            // 1.3 对比查询用户的密码,如果一直,使用JWT生成token,返回给前端
            String token = AppJwtUtil.getToken(apUser.getId().longValue());
            // 返回部分用户信息 对敏感信息处理
            apUser.setSalt(null);
            apUser.setPassword(null);
            apUser.setPhone(null);

            // 前端要求的返回信息
            HashMap<String, Object> map = new HashMap<>();
            map.put("user", apUser);
            map.put("token", token);
            return ResponseResult.okResult(map);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
    }

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private IApUserFollowService followService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topic.followBehaviorTopic}")
    private String followBehaviorTopic;

    @Override
    public ResponseResult follow(UserRelationDto dto) {
        // 获取当前登录的用户
        User user = AppThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 需要保存关注关系
        ApUserFollow userFollow = new ApUserFollow();
        userFollow.setUserId(user.getUserId());
        // 根据userId查询用户
        ApUser apUser = this.getById(user.getUserId());
        userFollow.setUserName(apUser.getName());
        userFollow.setLevel(1);
        userFollow.setCreatedTime(new Date());

        // 查询作者对应的ap_user_id
        ResponseResult<ApAuthor> authorResponseResult = articleFeign.getAuthorById(dto.getAuthorId());
        if (authorResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApAuthor apAuthor = authorResponseResult.getData();
            if (apAuthor != null) {
                userFollow.setFollowId(apAuthor.getUserId());
                userFollow.setFollowName(apAuthor.getName());

                // 判断当前的操作方式 0 关注 1 取关
                if (dto.getOperation() == 0) {
                    // 判断当前登录用户和关注用户是否已经有关注记录
                    LambdaQueryWrapper<ApUserFollow> query = new LambdaQueryWrapper<>();
                    query.eq(ApUserFollow::getUserId, user.getUserId());
                    query.eq(ApUserFollow::getFollowId, apAuthor.getUserId());
                    ApUserFollow follow = followService.getOne(query);
                    if (follow == null) {
                        // 新增
                        followService.save(userFollow);
                    }
                } else {
                    // 取消关注  删除对应关系
                    LambdaQueryWrapper<ApUserFollow> query = new LambdaQueryWrapper<>();
                    query.eq(ApUserFollow::getUserId, user.getUserId());
                    query.eq(ApUserFollow::getFollowId, apAuthor.getUserId());
                    followService.remove(query);
                }

                // 发送消息到kafka
                FollowBehaviorDto behaviorDto = new FollowBehaviorDto();
                behaviorDto.setUserId(user.getUserId());
                behaviorDto.setFollowId(apAuthor.getUserId());
                behaviorDto.setOperation(dto.getOperation());
                behaviorDto.setEquipmentId(dto.getEquipmentId());
                kafkaTemplate.send(followBehaviorTopic, JSON.toJSONString(behaviorDto));


            }
        }
        return ResponseResult.okResult();
    }
}
