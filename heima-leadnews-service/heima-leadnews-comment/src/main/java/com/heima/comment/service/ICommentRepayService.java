package com.heima.comment.service;

import com.heima.comment.dto.CommentRepayDto;
import com.heima.comment.dto.CommentRepayLikeDto;
import com.heima.comment.dto.CommentRepaySaveDto;
import com.heima.common.dto.ResponseResult;

public interface ICommentRepayService {
    ResponseResult save(CommentRepaySaveDto dto);

    ResponseResult like(CommentRepayLikeDto dto);

    ResponseResult load(CommentRepayDto dto);
}
