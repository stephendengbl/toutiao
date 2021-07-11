package com.heima.article.dto;

import lombok.Data;
import java.util.Date;
@Data
public class CollectionBehaviorDto {
    // 文章ID
    Long articleId;
    // 设备ID
    String equipmentId;
    // 文章、动态ID
    Long entryId;
    /**
     * 收藏内容类型
     * 0文章
     * 1动态
     */
    Integer type;

    /**
     * 操作类型
     * 0收藏
     * 1取消收藏
     */
    Integer operation;

    Date publishedTime;

}
