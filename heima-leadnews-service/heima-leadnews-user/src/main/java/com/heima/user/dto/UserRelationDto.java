package com.heima.user.dto;

import lombok.Data;

@Data
public class UserRelationDto {

    // 文章作者ID
    Integer authorId;

    // 文章id
    Long articleId;
    /**
     * 操作方式
     * 0  关注
     * 1  取消
     */
    Integer operation;
    // 设备id
    String equipmentId;
}
