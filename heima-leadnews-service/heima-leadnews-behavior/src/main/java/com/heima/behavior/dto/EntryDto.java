package com.heima.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {
    /**
     * 设备id
     */
    private String equipmentId;
    /**
     * 用户id
     */
    private Integer userId;
}
