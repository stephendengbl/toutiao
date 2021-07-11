package com.heima.behavior.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.dto.UnLikesBehaviorDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.behavior.entity.ApUnlikesBehavior;
import com.heima.behavior.mapper.ApUnlikesBehaviorMapper;
import com.heima.behavior.service.IApBehaviorEntryService;
import com.heima.behavior.service.IApUnlikesBehaviorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * APP不喜欢行为表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Service
public class ApUnlikesBehaviorServiceImpl extends ServiceImpl<ApUnlikesBehaviorMapper, ApUnlikesBehavior> implements IApUnlikesBehaviorService {

    @Autowired
    private IApBehaviorEntryService entryService;

    @Override
    public ResponseResult saveUnlikesBehavior(UnLikesBehaviorDto dto) {
        // 获取当前的用户
        User user = AppThreadLocalUtil.get();
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ApBehaviorEntry entry = entryService.getEntry(entryDto);

        // 构建对象
        ApUnlikesBehavior unlikesBehavior = new ApUnlikesBehavior();
        unlikesBehavior.setEntryId(entry.getId());
        unlikesBehavior.setArticleId(dto.getArticleId());
        unlikesBehavior.setType(dto.getType());
        unlikesBehavior.setCreatedTime(new Date());

        // 判断是否有不喜欢记录
        LambdaQueryWrapper<ApUnlikesBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApUnlikesBehavior::getEntryId, entry.getId());
        query.eq(ApUnlikesBehavior::getArticleId, dto.getArticleId());
        ApUnlikesBehavior apUnlikesBehavior = this.getOne(query);
        if (apUnlikesBehavior == null) {
            // 新增
            this.save(unlikesBehavior);
        } else {
            if (apUnlikesBehavior.getType() != dto.getType()) {
                apUnlikesBehavior.setType(dto.getType());
                this.updateById(apUnlikesBehavior);
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<ApUnlikesBehavior> getUnlikesBehavior(UnLikesBehaviorDto dto) {

        if(dto.getUserId()==null){
            User user = AppThreadLocalUtil.get();
            dto.setUserId(user.getUserId());
        }
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        entryDto.setUserId(dto.getUserId());
        ApBehaviorEntry entry = entryService.getEntry(entryDto);
        LambdaQueryWrapper<ApUnlikesBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApUnlikesBehavior::getEntryId, entry.getId());
        query.eq(ApUnlikesBehavior::getArticleId, dto.getArticleId());
        ApUnlikesBehavior apUnlikesBehavior = this.getOne(query);
        return ResponseResult.okResult(apUnlikesBehavior);
    }
}
