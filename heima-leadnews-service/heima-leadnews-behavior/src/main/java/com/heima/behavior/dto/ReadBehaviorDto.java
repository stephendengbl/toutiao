package com.heima.behavior.dto;

import lombok.Data;

@Data
public class ReadBehaviorDto {
    Integer userId;
    // 设备ID
    String equipmentId;
    // 文章、动态、评论等ID
    Long articleId;

    /**
     * 阅读次数
     */
    Integer count;

    /**
     * 阅读时长（S)
     */
    Integer readDuration;

    /**
     * 阅读百分比
     */
    Integer percentage;

    /**
     * 加载时间
     */
    Integer loadDuration;

}
