package com.heima.comment.controller;

import com.heima.comment.dto.CommentRepayDto;
import com.heima.comment.dto.CommentRepayLikeDto;
import com.heima.comment.dto.CommentRepaySaveDto;
import com.heima.comment.service.ICommentRepayService;
import com.heima.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment_repay")
public class CommentRepayController {

    @Autowired
    private ICommentRepayService repayService;

    @PostMapping("/save")
    public ResponseResult save(@RequestBody CommentRepaySaveDto dto){
        return repayService.save(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentRepayLikeDto dto){
        return repayService.like(dto);
    }

    @PostMapping("/load")
    public ResponseResult load(@RequestBody CommentRepayDto dto){
        return repayService.load(dto);
    }
}
