package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.admin.dto.SensitiveDto;
import com.heima.admin.entity.AdSensitive;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.IAdSensitiveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * <p>
 * 敏感词信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-18
 */
@Service
public class AdSensitiveServiceImpl extends ServiceImpl<AdSensitiveMapper, AdSensitive> implements IAdSensitiveService {

    @Override
    public ResponseResult listByName(SensitiveDto dto) {
        // 需求  根据敏感词模糊查询分页结果

        // 准备查询对象
        LambdaQueryWrapper<AdSensitive> query = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getName())) {
            query.like(AdSensitive::getSensitives, dto.getName());
        }
        // 准备分页请求对象
        IPage<AdSensitive> page = new Page<>(dto.getPage(), dto.getSize());
        // 调用分页接口
        IPage<AdSensitive> iPage = this.page(page, query);
        // 构建通用的分页返回响应
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(),
                iPage.getTotal(), iPage.getRecords());
        return result;
    }

    @Override
    public ResponseResult saveSensitive(AdSensitive entity) {

        // 需求  保存敏感词   先判断数据库中是否已经存在

        if (entity == null || StringUtils.isEmpty(entity.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 断数据库中是否已经存在
        LambdaQueryWrapper<AdSensitive> query = new LambdaQueryWrapper<>();
        query.eq(AdSensitive::getSensitives, entity.getSensitives());
        AdSensitive sensitive = this.getOne(query);
        if (sensitive == null) {
            // 添加时间
            entity.setCreatedTime(new Date());
            // 不存在新增
            this.save(entity);
        }
        return ResponseResult.okResult(entity.getId());
    }

    @Override
    public ResponseResult updateSensitive(AdSensitive entity) {
        // 需求 更新敏感词 先判断数据库中是否已经存在
        if (entity == null || StringUtils.isEmpty(entity.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 断数据库中是否已经存在
        LambdaQueryWrapper<AdSensitive> query = new LambdaQueryWrapper<>();
        query.eq(AdSensitive::getSensitives, entity.getSensitives());
        AdSensitive sensitive = this.getOne(query);
        // 如果已经存在,提示 敏感词已存在
        if (sensitive != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        // 更新
        this.updateById(entity);
        return ResponseResult.okResult();
    }


}
