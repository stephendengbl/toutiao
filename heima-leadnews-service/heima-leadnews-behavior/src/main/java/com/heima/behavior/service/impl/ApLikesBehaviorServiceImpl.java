package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.behavior.dto.EntryDto;
import com.heima.behavior.dto.LikesBehaviorDto;
import com.heima.behavior.dto.UpdateArticleMessage;
import com.heima.behavior.entity.ApBehaviorEntry;
import com.heima.behavior.entity.ApLikesBehavior;
import com.heima.behavior.mapper.ApLikesBehaviorMapper;
import com.heima.behavior.service.IApBehaviorEntryService;
import com.heima.behavior.service.IApLikesBehaviorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * APP点赞行为表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Service
public class ApLikesBehaviorServiceImpl extends ServiceImpl<ApLikesBehaviorMapper, ApLikesBehavior> implements IApLikesBehaviorService {

    @Autowired
    private IApBehaviorEntryService entryService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topic.hotArticleScoreTopic}")
    private String hotArticleScoreTopic;

    @Override
    public ResponseResult saveLikesBehavior(LikesBehaviorDto dto) {
        // 构建ApLikesBehavior对象
        ApLikesBehavior likesBehavior = new ApLikesBehavior();
        // 获取当前的用户
        User user = AppThreadLocalUtil.get();
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        ApBehaviorEntry entry = entryService.getEntry(entryDto);
        likesBehavior.setEntryId(entry.getId());
        likesBehavior.setArticleId(dto.getArticleId());
        likesBehavior.setType(0);
        likesBehavior.setOperation(dto.getOperation());
        likesBehavior.setCreatedTime(new Date());
        // 查询是否已经有点赞的记录
        LambdaQueryWrapper<ApLikesBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApLikesBehavior::getEntryId, entry.getId());
        query.eq(ApLikesBehavior::getArticleId, dto.getArticleId());
        ApLikesBehavior apLikesBehavior = this.getOne(query);
        if (apLikesBehavior != null) {
            if (apLikesBehavior.getOperation() != dto.getOperation()) {
                apLikesBehavior.setOperation(dto.getOperation());
                this.updateById(apLikesBehavior);
            }
        } else {
            // 保存
            this.save(likesBehavior);

            // 发送消息到kafka
            UpdateArticleMessage message = new UpdateArticleMessage();
            message.setType(1);
            message.setArticleId(dto.getArticleId());
            message.setAdd(1);
            kafkaTemplate.send(hotArticleScoreTopic, JSON.toJSONString(message));
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getLikesBehavior(LikesBehaviorDto dto) {
        // 获取当前的用户
        if (dto.getUserId() == null) {
            User user = AppThreadLocalUtil.get();
            dto.setUserId(user.getUserId());
        }
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        entryDto.setUserId(dto.getUserId());
        ApBehaviorEntry entry = entryService.getEntry(entryDto);
        // 查询是否已经有点赞的记录
        LambdaQueryWrapper<ApLikesBehavior> query = new LambdaQueryWrapper<>();
        query.eq(ApLikesBehavior::getEntryId, entry.getId());
        query.eq(ApLikesBehavior::getArticleId, dto.getArticleId());
        ApLikesBehavior apLikesBehavior = this.getOne(query);
        return ResponseResult.okResult(apLikesBehavior);
    }
}
