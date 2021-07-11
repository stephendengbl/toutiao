package com.heima.article.service;

import com.heima.article.dto.ArticleStreamMessage;

public interface IHotArticleService {

    public void compute();

    void update(ArticleStreamMessage articleStreamMessage);
}
