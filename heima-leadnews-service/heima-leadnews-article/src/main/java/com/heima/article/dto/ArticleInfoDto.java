package com.heima.article.dto;

import lombok.Data;

@Data
public class ArticleInfoDto {
    // 设备ID
    String equipmentId;
    // 文章ID
    Long articleId;
    // 作者ID
    Integer authorId;
}
