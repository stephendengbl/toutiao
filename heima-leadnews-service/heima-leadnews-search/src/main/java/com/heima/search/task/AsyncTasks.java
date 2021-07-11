package com.heima.search.task;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.search.dto.ApBehaviorEntry;
import com.heima.search.dto.EntryDto;
import com.heima.search.entity.ApUserSearch;
import com.heima.search.feign.BehaviorFeign;
import com.heima.search.service.IApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AsyncTasks {

    @Autowired
    private BehaviorFeign behaviorFeign;

    @Autowired
    private IApUserSearchService userSearchService;

    @Async
    public void saveSearchRecord(EntryDto entryDto, String keyword) {
        // 1. 保存用户的信息
        // 2. 查询行为实体 --远程调用
        ResponseResult<ApBehaviorEntry> entryResponseResult = behaviorFeign.getEntry(entryDto);
        if (entryResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApBehaviorEntry behaviorEntry = entryResponseResult.getData();
            ApUserSearch userSearch = new ApUserSearch();
            userSearch.setEntryId(behaviorEntry.getId());
            userSearch.setKeyword(keyword);
            userSearch.setCreatedTime(new Date());
            userSearch.setStatus(1);
            // 3. 判断是否已经有记录记录,并且判断状态是否已删除
            LambdaQueryWrapper<ApUserSearch> query = new LambdaQueryWrapper<>();
            query.eq(ApUserSearch::getEntryId, behaviorEntry.getId());
            query.eq(ApUserSearch::getKeyword, keyword);
            ApUserSearch one = userSearchService.getOne(query);
            if (one == null) {
                // 新增
                userSearchService.save(userSearch);
            } else {
                if (one.getStatus() == 0) {
                    one.setStatus(1);
                    userSearchService.updateById(one);
                }
            }
        }

    }
}
