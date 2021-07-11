package com.heima.behavior.dto;

import lombok.Data;

@Data
public class UpdateArticleMessage {
    /**
     * 操作类型 0 阅读 1 点赞 2 评论 3 收藏
      */
    private Integer type;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 修改数据的增量，可为正负
     */
    private Integer add;
}
