package com.heima.behavior.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.dto.FollowBehaviorDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.behavior.entity.ApFollowBehavior;
import com.heima.behavior.mapper.ApFollowBehaviorMapper;
import com.heima.behavior.service.IApBehaviorEntryService;
import com.heima.behavior.service.IApFollowBehaviorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * APP关注行为表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Service
public class ApFollowBehaviorServiceImpl extends ServiceImpl<ApFollowBehaviorMapper, ApFollowBehavior> implements IApFollowBehaviorService {


    @Autowired
    private IApBehaviorEntryService entryService;

    @Override
    public void saveBehavior(FollowBehaviorDto followBehaviorDto) {
        // 构建ApFollowBehavior 对象
        ApFollowBehavior followBehavior = new ApFollowBehavior();
        // 查询实体id
        EntryDto entryDto = new EntryDto(followBehaviorDto.getEquipmentId(), followBehaviorDto.getUserId());
        ApBehaviorEntry entry = entryService.getEntry(entryDto);
        followBehavior.setEntryId(entry.getId());
        followBehavior.setFollowId(followBehaviorDto.getFollowId());
        followBehavior.setOperation(followBehaviorDto.getOperation());
        followBehavior.setCreatedTime(new Date());
        // 判断是否已经有关注记录
        LambdaQueryWrapper<ApFollowBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApFollowBehavior::getEntryId, entry.getId());
        query.eq(ApFollowBehavior::getFollowId, followBehaviorDto.getFollowId());
        ApFollowBehavior apFollowBehavior = this.getOne(query);
        if (apFollowBehavior != null) {
            // 判断已有记录的operation 和传递过来的operation 是否一致,不一致才进行更新
            if (apFollowBehavior.getOperation() != followBehaviorDto.getOperation()) {
                apFollowBehavior.setOperation(followBehaviorDto.getOperation());
                this.updateById(apFollowBehavior);
            }
        } else {
            // 保存
            this.save(followBehavior);
        }

    }
}
