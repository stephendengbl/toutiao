package com.heima.behavior.service;

import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP行为实体表,一个行为实体可能是用户或者设备，或者其它 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
public interface IApBehaviorEntryService extends IService<ApBehaviorEntry> {

    ApBehaviorEntry getEntry(EntryDto entryDto);
}
