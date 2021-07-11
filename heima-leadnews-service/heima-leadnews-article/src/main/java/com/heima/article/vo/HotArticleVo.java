package com.heima.article.vo;

import com.heima.article.entity.ApArticle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HotArticleVo extends ApArticle {

    /**
     * 分值
     */
    private Integer score;
}
