package com.heima.behavior.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.behavior.mapper.ApBehaviorEntryMapper;
import com.heima.behavior.service.IApBehaviorEntryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * <p>
 * APP行为实体表,一个行为实体可能是用户或者设备，或者其它 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Service
public class ApBehaviorEntryServiceImpl extends ServiceImpl<ApBehaviorEntryMapper, ApBehaviorEntry> implements IApBehaviorEntryService {

    @Override
    public ApBehaviorEntry getEntry(EntryDto entryDto) {
        // 如果设备id和userId都为空 返回null
        if (entryDto == null || (entryDto.getUserId() == null && StringUtils.isEmpty(entryDto.getEquipmentId()))) {
            return null;
        }
        // 如果userId不为空,根据userId查询
        if (entryDto.getUserId() != null) {
            LambdaQueryWrapper<ApBehaviorEntry> query = new LambdaQueryWrapper<>();
            query.eq(ApBehaviorEntry::getType, 1);
            query.eq(ApBehaviorEntry::getEntryId, entryDto.getUserId().toString());
            ApBehaviorEntry apBehaviorEntry = this.getOne(query);
            if (apBehaviorEntry != null) {
                return apBehaviorEntry;
            } else {
                // 如果实体为空,新增
                ApBehaviorEntry behaviorEntry = new ApBehaviorEntry();
                behaviorEntry.setType(1);
                behaviorEntry.setEntryId(entryDto.getUserId().toString());
                behaviorEntry.setCreatedTime(new Date());
                this.save(behaviorEntry);
                return behaviorEntry;
            }
        }
        // 如果设备Id不为空,根据设备Id查询
        if (!StringUtils.isEmpty(entryDto.getEquipmentId())) {
            LambdaQueryWrapper<ApBehaviorEntry> query = new LambdaQueryWrapper<>();
            query.eq(ApBehaviorEntry::getType, 0);
            query.eq(ApBehaviorEntry::getEntryId, entryDto.getEquipmentId());
            ApBehaviorEntry apBehaviorEntry = this.getOne(query);
            if (apBehaviorEntry != null) {
                return apBehaviorEntry;
            } else {
                // 如果实体为空,新增
                ApBehaviorEntry behaviorEntry = new ApBehaviorEntry();
                behaviorEntry.setType(0);
                behaviorEntry.setEntryId(entryDto.getEquipmentId());
                behaviorEntry.setCreatedTime(new Date());
                this.save(behaviorEntry);
                return behaviorEntry;
            }
        }
        return null;
    }
}
