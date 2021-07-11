package com.heima.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppThreadLocalUtil;
import com.heima.search.dto.ApBehaviorEntry;
import com.heima.search.dto.EntryDto;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.entity.ApUserSearch;
import com.heima.search.feign.BehaviorFeign;
import com.heima.search.mapper.ApUserSearchMapper;
import com.heima.search.service.IApUserSearchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * APP用户搜索信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
@Service
public class ApUserSearchServiceImpl extends ServiceImpl<ApUserSearchMapper, ApUserSearch> implements IApUserSearchService {


    @Autowired
    private BehaviorFeign behaviorFeign;

    @Override
    public ResponseResult load(UserSearchDto dto) {

        // 需求
        // 1. 默认查询最近的5条搜素记录
        // 2. 状态为1的数据
        // 3. 查询当前用户自己的搜索记录
        // 4. 根据创建时间倒序排列
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        User user = AppThreadLocalUtil.get();
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ResponseResult<ApBehaviorEntry> entryResponseResult = behaviorFeign.getEntry(entryDto);
        if (entryResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApBehaviorEntry entry = entryResponseResult.getData();

            LambdaQueryWrapper<ApUserSearch> query = new LambdaQueryWrapper<>();
            query.eq(ApUserSearch::getEntryId, entry.getId());
            query.eq(ApUserSearch::getStatus, 1);
            // 默认查询最近的5条搜素记录
            if (dto.getSize() == null || dto.getSize() <= 0 || dto.getSize() > 20) {
                dto.setSize(5);
            }
            query.orderByDesc(ApUserSearch::getCreatedTime);
            IPage<ApUserSearch> page = new Page<>(1, dto.getSize());
            IPage<ApUserSearch> iPage = this.page(page, query);
            return ResponseResult.okResult(iPage.getRecords());
        }
        List<ApUserSearch> list = new ArrayList<>();
        return ResponseResult.okResult(list);
    }

    @Override
    public ResponseResult del(UserSearchDto dto) {
        // 1. 根据当前用户删除
        // 2. 删除对应的id
        // 3. 更新状态为0
        List<ApUserSearch> hisList = dto.getHisList();
        List<Integer> ids = new ArrayList<>();
        for (ApUserSearch userSearch : hisList) {
            ids.add(userSearch.getId());
        }
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        User user = AppThreadLocalUtil.get();
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ResponseResult<ApBehaviorEntry> entryResponseResult = behaviorFeign.getEntry(entryDto);
        if (entryResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApBehaviorEntry entry = entryResponseResult.getData();
            // 更新
            LambdaUpdateWrapper<ApUserSearch> update = new LambdaUpdateWrapper<>();
            update.eq(ApUserSearch::getEntryId, entry.getId());
            update.in(ApUserSearch::getId, ids);
            update.set(ApUserSearch::getStatus, 0);
            this.update(update);
        }

        return ResponseResult.okResult();
    }
}
