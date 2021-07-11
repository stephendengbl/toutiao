package com.heima.behavior.dto;

import lombok.Data;
@Data
public class UnLikesBehaviorDto {
    Integer userId;
    // 设备ID
    String equipmentId;
    // 文章ID
    Long articleId;

    /**
     * 不喜欢操作方式
     * 0 不喜欢
     * 1 取消不喜欢
     */
    Integer type;
}
