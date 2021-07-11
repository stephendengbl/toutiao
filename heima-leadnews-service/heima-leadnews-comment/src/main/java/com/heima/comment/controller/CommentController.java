package com.heima.comment.controller;

import com.heima.comment.dto.CommentDto;
import com.heima.comment.dto.CommentLikeDto;
import com.heima.comment.dto.CommentSaveDto;
import com.heima.comment.service.ICommentService;
import com.heima.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @PostMapping("/save")
    public ResponseResult saveComment(@RequestBody CommentSaveDto dto) {
        return commentService.saveComment(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto) {
        return commentService.like(dto);
    }

    @PostMapping("/load")
    public ResponseResult load(@RequestBody CommentDto dto){
        return commentService.load(dto);
    }
}
