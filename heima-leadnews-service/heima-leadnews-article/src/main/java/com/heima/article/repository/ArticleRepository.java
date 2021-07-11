package com.heima.article.repository;

import com.heima.article.entity.ApArticleSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<ApArticleSearch, Long> {
}
