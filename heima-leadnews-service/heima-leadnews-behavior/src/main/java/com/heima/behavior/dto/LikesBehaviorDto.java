package com.heima.behavior.dto;

import lombok.Data;

@Data
public class LikesBehaviorDto {
    // 用户id
    Integer userId;
    // 设备ID
    String equipmentId;
    // 文章、动态、评论等ID
    Long articleId;
    /**
     * 喜欢内容类型
     * 0文章
     * 1动态
     * 2评论
     */
    Integer type;

    /**
     * 喜欢操作方式
     * 0 点赞
     * 1 取消点赞
     */
    Integer operation;
}
