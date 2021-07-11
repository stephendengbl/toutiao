package com.heima.search.service.impl;

import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.util.AppThreadLocalUtil;
import com.heima.search.dto.ApArticleSearchDto;
import com.heima.search.dto.EntryDto;
import com.heima.search.entity.ApArticleSearch;
import com.heima.search.service.IArticleSearchService;
import com.heima.search.task.AsyncTasks;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleSearchServiceImpl implements IArticleSearchService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public ResponseResult searchArticle(ApArticleSearchDto dto) {

        // 需求分析
        // 1 根据关键字查询标题
        // 2 分页查询
        // 3 查询结果按照发布时间倒序排列
        if (StringUtils.isEmpty(dto.getSearchWords())) {
            List<ApArticleSearch> searchList = new ArrayList<>();
            return ResponseResult.okResult(searchList);
        }
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", dto.getSearchWords());
        // 构建分页条件
        Pageable pageable = PageRequest.of(dto.getPage().intValue() - 1, dto.getSize(), Sort.Direction.DESC, "publishTime");
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .build();
        AggregatedPage<ApArticleSearch> queryForPage = elasticsearchRestTemplate.queryForPage(query, ApArticleSearch.class);
        List<ApArticleSearch> searchList = queryForPage.getContent();

        // 异步保存用户搜索记录
        EntryDto entryDto = new EntryDto();
        entryDto.setEquipmentId(dto.getEquipmentId());
        User user = AppThreadLocalUtil.get();
        if (user != null) {
            entryDto.setUserId(user.getUserId());
        }
        tasks.saveSearchRecord(entryDto, dto.getSearchWords());
        return ResponseResult.okResult(searchList);
    }

    @Autowired
    private AsyncTasks tasks;
}
