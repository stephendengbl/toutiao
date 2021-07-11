package com.heima.admin.repository;

import com.heima.admin.entity.ApArticleSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<ApArticleSearch, Long> {
}
