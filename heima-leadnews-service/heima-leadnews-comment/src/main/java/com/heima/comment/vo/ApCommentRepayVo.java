package com.heima.comment.vo;

import com.heima.comment.entity.ApCommentRepay;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApCommentRepayVo extends ApCommentRepay {

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Integer operation;
}
