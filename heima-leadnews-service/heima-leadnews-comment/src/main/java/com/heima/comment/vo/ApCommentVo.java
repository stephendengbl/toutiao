package com.heima.comment.vo;

import com.heima.comment.entity.ApComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApCommentVo extends ApComment {

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Integer operation;
}
