package com.heima.behavior.dto;

import lombok.Data;

@Data
public class FollowBehaviorDto {
    /**
     * 关注id
     */
    private Integer followId;
    /**
     * 设备id
     */
    private String equipmentId;
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 操作类型 0  关注  1 取消关注
     */
    private Integer operation;

}
