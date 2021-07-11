package com.heima.article;

import com.heima.article.entity.ApArticle;
import com.heima.article.entity.ApArticleSearch;
import com.heima.article.repository.ArticleRepository;
import com.heima.article.service.IApArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ArticleSyncTests {

    @Autowired
    private IApArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void sync() {
        // 1. 从MySQL中查询所有的文章数据
        List<ApArticle> articles = articleService.list();
        // 2. 保存数据到ES的索引中
        List<ApArticleSearch> searchList = new ArrayList<>();
        for (ApArticle article : articles) {
            ApArticleSearch articleSearch = new ApArticleSearch();
            articleSearch.setId(article.getId());
            articleSearch.setTitle(article.getTitle());
            articleSearch.setLayout(article.getLayout());
            articleSearch.setImages(article.getImages());
            articleSearch.setPublishTime(article.getPublishTime());
            searchList.add(articleSearch);
        }

        articleRepository.saveAll(searchList);
    }
}
