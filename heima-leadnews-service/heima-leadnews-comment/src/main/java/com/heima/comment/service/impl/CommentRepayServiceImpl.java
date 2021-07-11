package com.heima.comment.service.impl;

import com.heima.comment.dto.ApUser;
import com.heima.comment.dto.CommentRepayDto;
import com.heima.comment.dto.CommentRepayLikeDto;
import com.heima.comment.dto.CommentRepaySaveDto;
import com.heima.comment.entity.ApComment;
import com.heima.comment.entity.ApCommentRepay;
import com.heima.comment.entity.ApCommentRepayLike;
import com.heima.comment.feign.UserFeign;
import com.heima.comment.service.ICommentRepayService;
import com.heima.comment.vo.ApCommentRepayVo;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class CommentRepayServiceImpl implements ICommentRepayService {

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult save(CommentRepaySaveDto dto) {
        // 需求分析
        if (dto == null || dto.getCommentId() == null || StringUtils.isEmpty(dto.getContent())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 1. 评论内容需要小于等于140字
        if (dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2  要求登录
        User user = AppThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 3 文本内容安全过滤  敏感词审核  阿里云文本审核  TODO

        // 4 保存评论回复
        ApCommentRepay comment = new ApCommentRepay();
        comment.setAuthorId(user.getUserId());
        // 远程调用用户微服务查询用户
        ResponseResult<ApUser> userResponseResult = userFeign.getUserById(user.getUserId());
        if (userResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApUser apUser = userResponseResult.getData();
            comment.setAuthorName(apUser.getName());
            comment.setCommentId(dto.getCommentId());
            comment.setContent(dto.getContent());
            comment.setLikes(0);
            comment.setCreatedTime(new Date());
            mongoTemplate.save(comment);
            // 5 回复后更新评论的回复数量+1
            Query queryComment = new Query(Criteria.where("id").is(dto.getCommentId()));
            Update update = new Update();
            update.inc("reply");
            mongoTemplate.updateFirst(queryComment,update, ApComment.class);
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult like(CommentRepayLikeDto dto) {
        // 需求分析
        // 1 分析谁来进行点赞
        User user = AppThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 2 保存点赞的数据
        ApCommentRepayLike commentLike = new ApCommentRepayLike();
        commentLike.setAuthorId(user.getUserId());
        commentLike.setCommentRepayId(dto.getCommentRepayId());
        commentLike.setOperation(dto.getOperation());
        // 查询是否已经有该用户对此评论的点赞记录
        Query query = new Query();
        query.addCriteria(Criteria.where("authorId").is(user.getUserId()));
        query.addCriteria(Criteria.where("commentId").is(dto.getCommentRepayId()));
        ApCommentRepayLike one = mongoTemplate.findOne(query, ApCommentRepayLike.class);
        if (one == null) {
            // 新增
            mongoTemplate.save(commentLike);
            // 更新评论的点赞数+1
            Query queryComment = new Query(Criteria.where("id").is(dto.getCommentRepayId()));
            Update update = new Update();
            update.inc("likes");
            mongoTemplate.updateFirst(queryComment, update, ApCommentRepay.class);
        } else {
            // 判断数据库中的操作状态是否和传入的状态一致,不一致进行更新
            if (one.getOperation() != dto.getOperation()) {
                one.setOperation(dto.getOperation());
                mongoTemplate.save(one);
                int inc = 1;
                if (dto.getOperation() == 1) {
                    // 取消点赞,更新评论的点赞数-1
                    inc = -1;
                }
                Query queryComment = new Query(Criteria.where("id").is(dto.getCommentRepayId()));
                Update update = new Update();
                update.inc("likes", inc);
                mongoTemplate.updateFirst(queryComment, update, ApCommentRepay.class);
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult load(CommentRepayDto dto) {
        // 需求分析
        Query query = new Query();
        // 1 根据文章id查询
        query.addCriteria(Criteria.where("commentId").is(dto.getCommentId()));
        // 2 默认每页显示10条数据
        // 3 列表按照发布时间倒序
        // 构建分页条件  参数  1  当前页 2 每页条数  3 排序方向 4 排序字段
        Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "createdTime");
        query.with(page);
        // 4 分页查询时接收前端的最小时间,判断比这个时间更早的数据
        query.addCriteria(Criteria.where("createdTime").lt(dto.getMinDate()));
        List<ApCommentRepay> commentList = mongoTemplate.find(query, ApCommentRepay.class);

        // 4 判断用户是否登录 如果用户没有登录,直接返回评论列表 否则 返回带有状态的列表
        User user = AppThreadLocalUtil.get();
        List<ApCommentRepayVo> vos = new ArrayList<>();
        // 获取当前列表中所有的评论id
        List<String> ids = new ArrayList<>();
        for (ApCommentRepay comment : commentList) {
            ids.add(comment.getId());
            ApCommentRepayVo vo = new ApCommentRepayVo();
            // 属性赋值
            BeanUtils.copyProperties(comment, vo);
            vo.setOperation(1);
            vos.add(vo);
        }
        if (user == null) {
            return ResponseResult.okResult(vos);
        }
        // 用户登录 需要判断当前用户在当前的评论列表中是否已经点赞了
        // 查询点赞记录
        Query likeQuery = new Query();
        likeQuery.addCriteria(Criteria.where("authorId").is(user.getUserId()));
        likeQuery.addCriteria(Criteria.where("commentRepayId").in(ids));
        List<ApCommentRepayLike> apCommentLikes = mongoTemplate.find(likeQuery, ApCommentRepayLike.class);
        Map<String, Integer> map = new HashMap<>();
        for (ApCommentRepayLike apCommentLike : apCommentLikes) {
            if (apCommentLike.getOperation() == 0) {
                map.put(apCommentLike.getCommentRepayId(), 0);
            }
        }
        // 遍历最终返回的结果
        for (ApCommentRepayVo vo : vos) {
            if (map.containsKey(vo.getId())) {
                vo.setOperation(0);
            }
        }
        return ResponseResult.okResult(vos);
    }
}
