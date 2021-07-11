package com.heima.search.controller;

import com.heima.common.dto.ResponseResult;
import com.heima.search.dto.ApArticleSearchDto;
import com.heima.search.service.IArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article/search")
public class ArticleSearchController {

    @Autowired
    private IArticleSearchService articleSearchService;

    @PostMapping("/search")
    public ResponseResult searchArticle(@RequestBody ApArticleSearchDto dto) {
        return articleSearchService.searchArticle(dto);
    }
}
