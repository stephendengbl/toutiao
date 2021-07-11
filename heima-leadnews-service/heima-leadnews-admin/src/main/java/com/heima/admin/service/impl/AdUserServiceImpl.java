package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.admin.dto.AdUserDto;
import com.heima.admin.entity.AdUser;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.IAdUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppJwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * <p>
 * 管理员用户信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-18
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements IAdUserService {

    @Override
    public ResponseResult login(AdUserDto dto) {

        // 判断用户名和密码是否都为空
        if (dto == null || StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 根据用户名查询用户
        LambdaQueryWrapper<AdUser> query = new LambdaQueryWrapper<>();
        query.eq(AdUser::getName, dto.getName());
        AdUser adUser = this.getOne(query);
        if (adUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 使用传入的明文密码+查询用户的盐生成MD5密文
        String pwd = dto.getPassword() + adUser.getSalt();
        // 计算md5
        String pass = DigestUtils.md5DigestAsHex(pwd.getBytes());
        // 使用密文和表中的密码进行对比
        if (!pass.equals(adUser.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        // 一致 生成token  使用用户id生成token
        String token = AppJwtUtil.getToken(adUser.getId().longValue());

        // 返回部分用户信息 对敏感信息处理
        adUser.setSalt(null);
        adUser.setPassword(null);
        adUser.setPhone(null);

        // 前端要求的返回信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", adUser);
        map.put("token", token);
        return ResponseResult.okResult(map);
    }
}
