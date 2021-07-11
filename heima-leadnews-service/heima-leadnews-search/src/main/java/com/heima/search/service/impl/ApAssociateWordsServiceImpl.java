package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.dto.ResponseResult;
import com.heima.common.util.Trie;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.entity.ApAssociateWords;
import com.heima.search.mapper.ApAssociateWordsMapper;
import com.heima.search.service.IApAssociateWordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 联想词表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-06-01
 */
@Service
public class ApAssociateWordsServiceImpl extends ServiceImpl<ApAssociateWordsMapper, ApAssociateWords> implements IApAssociateWordsService {

    @Override
    public ResponseResult load(UserSearchDto dto) {

        // 需求
        // 1. 根据用户输入的关键词查询
        if (StringUtils.isEmpty(dto.getSearchWords())) {
            List<String> list = new ArrayList<>();
            return ResponseResult.okResult(list);
        }
        // 2. 每页返回10条数据
        IPage<ApAssociateWords> page = new Page<>(1, 10);
        LambdaQueryWrapper<ApAssociateWords> query = new LambdaQueryWrapper<>();
        query.likeRight(ApAssociateWords::getAssociateWords, dto.getSearchWords());
        IPage<ApAssociateWords> iPage = this.page(page, query);
        return ResponseResult.okResult(iPage.getRecords());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult load2(UserSearchDto dto) {
        // 需求
        // 1. 根据用户输入的关键词查询
        if (StringUtils.isEmpty(dto.getSearchWords())) {
            List<String> list = new ArrayList<>();
            return ResponseResult.okResult(list);
        }
        // 2. 判断redis中是否有记录,有的话解析成集合 ,没有需要添加
        String key = "associate_words";
        String json = redisTemplate.opsForValue().get(key);
        List<ApAssociateWords> words = new ArrayList<>();
        if (StringUtils.isEmpty(json)) {
            // 查询联想词表,保存数据到redis中
            words = this.list();
            redisTemplate.opsForValue().set(key, JSON.toJSONString(words));
        } else {
            words = JSON.parseArray(json, ApAssociateWords.class);
        }
        // 3. 构建字典树查询
        Trie trie = new Trie();
        for (ApAssociateWords word : words) {
            trie.insert(word.getAssociateWords());
        }
        List<String> list = trie.startWith(dto.getSearchWords());
        List<ApAssociateWords> result = new ArrayList<>();
        for (String s : list) {
            ApAssociateWords associateWords = new ApAssociateWords();
            associateWords.setAssociateWords(s);
            result.add(associateWords);
        }
        return ResponseResult.okResult(result);
    }
}
