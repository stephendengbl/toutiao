package com.heima.behavior.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.dto.ReadBehaviorDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.behavior.entity.ApLikesBehavior;
import com.heima.behavior.entity.ApReadBehavior;
import com.heima.behavior.mapper.ApReadBehaviorMapper;
import com.heima.behavior.service.IApBehaviorEntryService;
import com.heima.behavior.service.IApReadBehaviorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * APP阅读行为表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Service
public class ApReadBehaviorServiceImpl extends ServiceImpl<ApReadBehaviorMapper, ApReadBehavior> implements IApReadBehaviorService {

    @Autowired
    private IApBehaviorEntryService entryService;

    @Override
    public ResponseResult saveReadBehavior(ReadBehaviorDto dto) {
        // 构建ApReadBehavior对象
        // 获取实体id
        // 获取当前的用户
        User user = AppThreadLocalUtil.get();
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ApBehaviorEntry entry = entryService.getEntry(entryDto);
        ApReadBehavior readBehavior = new ApReadBehavior();
        readBehavior.setEntryId(entry.getId());
        readBehavior.setArticleId(dto.getArticleId());
        readBehavior.setCount(1);
        readBehavior.setReadDuration(dto.getReadDuration());
        readBehavior.setPercentage(dto.getPercentage());
        readBehavior.setLoadDuration(dto.getLoadDuration());
        readBehavior.setCreatedTime(new Date());

        // 判断是否已经有阅读记录
        LambdaQueryWrapper<ApReadBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApReadBehavior::getEntryId, entry.getId());
        query.eq(ApReadBehavior::getArticleId, dto.getArticleId());
        ApReadBehavior apReadBehavior = this.getOne(query);
        if (apReadBehavior == null) {
            // 保存
            this.save(readBehavior);
        } else {
            apReadBehavior.setLoadDuration(dto.getLoadDuration());
            apReadBehavior.setReadDuration(dto.getReadDuration());
            apReadBehavior.setPercentage(dto.getPercentage());
            // 在高并发的情况下有可能出现数据不一致的问题
            // 方案  1  加锁  分布式锁
            // 方案  2  原子类
            // 方案  3  update  ap_read_behavior set count = count+1 where id = #id
            apReadBehavior.setCount(apReadBehavior.getCount() + 1);
            apReadBehavior.setUpdatedTime(new Date());
            // this.updateById(apReadBehavior);

            LambdaUpdateWrapper<ApReadBehavior> update = new LambdaUpdateWrapper<>();
            // 查询条件
            update.eq(ApReadBehavior::getId, apReadBehavior.getId());
            // 更新的字段
            update.set(ApReadBehavior::getLoadDuration, dto.getLoadDuration());
            update.set(ApReadBehavior::getReadDuration, dto.getReadDuration());
            update.set(ApReadBehavior::getPercentage, dto.getPercentage());
            update.set(ApReadBehavior::getUpdatedTime, new Date());
            update.setSql("count = count+1");
            this.update(update);
        }
        return ResponseResult.okResult();
    }
}
